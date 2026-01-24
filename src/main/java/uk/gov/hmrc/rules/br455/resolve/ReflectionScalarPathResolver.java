package uk.gov.hmrc.rules.br455.resolve;

// Version: 2026-01-24

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ReflectionScalarPathResolver {

    private final ResolutionStrategy strict;
    private final ResolutionStrategy permissive;

    public ReflectionScalarPathResolver(ResolutionStrategy strict, ResolutionStrategy permissive) {
        this.strict = (strict == null) ? new StrictResolutionStrategy() : strict;
        this.permissive = (permissive == null) ? new StrictResolutionStrategy() : permissive;
    }

    /**
     * Two-pass:
     * - Pass 1: strict strategy (no tail-collapse, no map)
     * - Pass 2: permissive strategy (tail-collapse + map)
     */
    public PathResolution resolveScalarTwoPass(Class<?> rootType, String[] parts, int startIndex) {
        try {
            return resolveScalar(rootType, parts, startIndex, strict);
        } catch (RuntimeException first) {
            log.debug("Strict resolution failed, retrying with permissive strategy");
            return resolveScalar(rootType, parts, startIndex, permissive);
        }
    }

    /**
     * Single-pass navigation using reflection; strategy is consulted only after default lookup fails.
     */
    public PathResolution resolveScalar(Class<?> rootType, String[] parts, int startIndex, ResolutionStrategy strategy) {

        if (rootType == null) {
            throw new IllegalArgumentException("rootType must not be null");
        }
        if (parts == null || parts.length == 0) {
            throw new IllegalArgumentException("parts must not be null/empty");
        }
        if (startIndex < 0 || startIndex >= parts.length) {
            throw new IllegalArgumentException("startIndex out of range: " + startIndex + " (parts length " + parts.length + ")");
        }
        if (strategy == null) {
            throw new IllegalArgumentException("strategy must not be null");
        }

        Class<?> currentType = rootType;
        java.util.ArrayList<String> resolvedSegments = new java.util.ArrayList<>();

        for (int i = startIndex; i < parts.length; i++) {

            String original = parts[i];
            String normalised = normaliseSegment(original);

            // DEFAULT LOOKUP (no strategy yet)
            PropertyHit hit = findProperty(currentType, normalised);
            if (hit != null) {
                resolvedSegments.add(hit.droolsPropertyName);
                currentType = hit.nextType;
                continue;
            }

            // Strategy kicks in only now (default failed)
            PropertyHit recovered = tryStrategyCandidates(currentType, original, strategy);
            if (recovered != null) {
                resolvedSegments.add(recovered.droolsPropertyName);
                currentType = recovered.nextType;
                continue;
            }

            // Tail-collapse attempt (e.g. type.code -> typeCode)
            java.util.Optional<String> tail = strategy.tailCollapseCandidate(currentType, parts, i);
            if (tail.isPresent()) {

                String collapsed = normaliseSegment(tail.get());
                PropertyHit tailHit = findProperty(currentType, collapsed);

                if (tailHit != null) {
                    resolvedSegments.add(tailHit.droolsPropertyName);
                    return new PathResolution(String.join(".", resolvedSegments));
                }
            }

            throw new IllegalArgumentException(
                    "Cannot resolve segment '" + original + "' (normalised '" + normalised + "') on type " + currentType.getName()
                            + " (root " + rootType.getName() + ", index " + i + ")"
            );
        }

        return new PathResolution(String.join(".", resolvedSegments));
    }

    private PropertyHit tryStrategyCandidates(Class<?> currentType, String originalSegment, ResolutionStrategy strategy) {

        java.util.List<String> candidates = strategy.segmentCandidates(currentType, originalSegment);
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        for (String c : candidates) {
            if (c == null || c.isBlank()) {
                continue;
            }
            String normalised = normaliseSegment(c);
            PropertyHit hit = findProperty(currentType, normalised);
            if (hit != null) {
                return hit;
            }
        }

        return null;
    }

    /**
     * Default hygiene:
     * - trim
     * - PascalCase -> camelCase (only first char)
     */
    private String normaliseSegment(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        if (t.isEmpty()) {
            return t;
        }
        if (Character.isUpperCase(t.charAt(0))) {
            return t.substring(0, 1).toLowerCase(java.util.Locale.ROOT) + t.substring(1);
        }
        return t;
    }

    // ----------------------------
    // Reflection plumbing
    // ----------------------------

    private PropertyHit findProperty(Class<?> type, String name) {

        // 1) Public JavaBean getter getXxx() / isXxx()
        java.lang.reflect.Method getter = findGetter(type, name);
        if (getter != null) {
            Class<?> returnType = getter.getReturnType();
            Class<?> next = unwrapCollectionElementType(getter.getGenericReturnType(), returnType);
            return new PropertyHit(name, next);
        }

        // 2) Field
        java.lang.reflect.Field field = findField(type, name);
        if (field != null) {
            Class<?> raw = field.getType();
            Class<?> next = unwrapCollectionElementType(field.getGenericType(), raw);
            return new PropertyHit(field.getName(), next);
        }

        // 3) Record component accessor
        if (type.isRecord()) {
            java.lang.reflect.RecordComponent[] comps = type.getRecordComponents();
            for (java.lang.reflect.RecordComponent c : comps) {
                if (c.getName().equals(name)) {
                    Class<?> raw = c.getType();
                    Class<?> next = unwrapCollectionElementType(c.getGenericType(), raw);
                    return new PropertyHit(c.getName(), next);
                }
            }
        }

        return null;
    }

    private java.lang.reflect.Method findGetter(Class<?> type, String name) {
        String cap = capitalise(name);

        java.lang.reflect.Method m = findNoArgMethod(type, "get" + cap);
        if (m != null) {
            return m;
        }

        m = findNoArgMethod(type, "is" + cap);
        if (m != null && (m.getReturnType() == boolean.class || m.getReturnType() == Boolean.class)) {
            return m;
        }

        return null;
    }

    private java.lang.reflect.Method findNoArgMethod(Class<?> type, String methodName) {

        // Public first (includes inherited)
        try {
            return type.getMethod(methodName);
        } catch (NoSuchMethodException ignored) {
            // fall through
        }

        // Declared across hierarchy
        Class<?> t = type;
        while (t != null && t != Object.class) {
            try {
                java.lang.reflect.Method m = t.getDeclaredMethod(methodName);
                try {
                    m.setAccessible(true);
                } catch (RuntimeException ignored) {
                    // Java 17 module rules may block this; okay.
                }
                return m;
            } catch (NoSuchMethodException ignored) {
                // continue
            }
            t = t.getSuperclass();
        }

        return null;
    }

    private java.lang.reflect.Field findField(Class<?> type, String name) {

        // Public first (includes inherited)
        try {
            return type.getField(name);
        } catch (NoSuchFieldException ignored) {
            // fall through
        }

        // Declared across hierarchy
        Class<?> t = type;
        while (t != null && t != Object.class) {
            try {
                java.lang.reflect.Field f = t.getDeclaredField(name);
                try {
                    f.setAccessible(true);
                } catch (RuntimeException ignored) {
                    // Java 17 module rules may block this; okay.
                }
                return f;
            } catch (NoSuchFieldException ignored) {
                // continue
            }
            t = t.getSuperclass();
        }

        return null;
    }

    private Class<?> unwrapCollectionElementType(java.lang.reflect.Type genericType, Class<?> rawType) {

        if (rawType.isArray()) {
            return rawType.getComponentType();
        }

        if (java.util.Collection.class.isAssignableFrom(rawType)) {
            if (genericType instanceof java.lang.reflect.ParameterizedType pt) {
                java.lang.reflect.Type[] args = pt.getActualTypeArguments();
                if (args.length == 1) {
                    java.lang.reflect.Type arg = args[0];
                    if (arg instanceof Class<?> c) {
                        return c;
                    }
                    if (arg instanceof java.lang.reflect.ParameterizedType p2 && p2.getRawType() instanceof Class<?> c2) {
                        return c2;
                    }
                }
            }
            // If we can't infer element type, we still allow navigation but future lookup will likely fail.
            return Object.class;
        }

        return rawType;
    }

    private String capitalise(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        if (s.length() == 1) {
            return s.toUpperCase(java.util.Locale.ROOT);
        }
        return s.substring(0, 1).toUpperCase(java.util.Locale.ROOT) + s.substring(1);
    }

    private static final class PropertyHit {
        private final String droolsPropertyName;
        private final Class<?> nextType;

        private PropertyHit(String droolsPropertyName, Class<?> nextType) {
            this.droolsPropertyName = droolsPropertyName;
            this.nextType = nextType;
        }
    }
}
