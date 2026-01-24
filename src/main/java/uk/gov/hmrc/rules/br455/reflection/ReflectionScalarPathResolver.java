package uk.gov.hmrc.rules.br455.reflection;

// Version: 2026-01-24
public final class ReflectionScalarPathResolver {

    private final ExcelAliasResolver aliasResolver;

    public ReflectionScalarPathResolver(ExcelAliasResolver aliasResolver) {
        this.aliasResolver = (aliasResolver == null) ? new NoOpExcelAliasResolver() : aliasResolver;
    }

    public PathResolution resolveScalar(Class<?> rootType, String[] parts, int startIndex) {

        if (rootType == null) {
            throw new IllegalArgumentException("rootType must not be null");
        }
        if (parts == null || parts.length == 0) {
            throw new IllegalArgumentException("parts must not be null/empty");
        }
        if (startIndex < 0 || startIndex >= parts.length) {
            throw new IllegalArgumentException("startIndex out of range: " + startIndex + " (parts length " + parts.length + ")");
        }

        Class<?> currentType = rootType;
        java.util.ArrayList<String> resolvedSegments = new java.util.ArrayList<>();

        for (int i = startIndex; i < parts.length; i++) {

            String spreadsheetName = parts[i];
            String javaName = aliasResolver.toJavaName(rootType, parts, startIndex, i, currentType, spreadsheetName);

            PropertyHit hit = findProperty(currentType, javaName);
            if (hit == null && !javaName.equals(spreadsheetName)) {
                // If alias changed the name and still no hit, try the original name too.
                hit = findProperty(currentType, spreadsheetName);
                javaName = spreadsheetName;
            }

            if (hit == null) {
                throw new IllegalArgumentException(
                        "Cannot resolve segment '" + spreadsheetName + "' (javaName '" + javaName + "') on type " + currentType.getName()
                                + " while resolving from root " + rootType.getName()
                );
            }

            // Drools path segment name should match the property name used in DRL/DSL
            resolvedSegments.add(hit.droolsPropertyName);

            // Move to next type for deeper navigation (handle collections reasonably)
            currentType = hit.nextType;
            if (currentType == null) {
                throw new IllegalArgumentException(
                        "Resolved segment '" + hit.droolsPropertyName + "' but nextType was null on type " + currentType
                );
            }
        }

        String path = String.join(".", resolvedSegments);
        return new PathResolution(path);
    }

    // ----------------------------
    // Helpers (kept small + predictable)
    // ----------------------------

    private PropertyHit findProperty(Class<?> type, String name) {

        // 1) Try JavaBean getter: getXxx() / isXxx()
        java.lang.reflect.Method getter = findGetter(type, name);
        if (getter != null) {
            Class<?> returnType = getter.getReturnType();
            Class<?> next = unwrapCollectionElementType(getter.getGenericReturnType(), returnType);
            return new PropertyHit(name, next);
        }

        // 2) Try field: name or snake/case variants could be added later
        java.lang.reflect.Field field = findField(type, name);
        if (field != null) {
            Class<?> raw = field.getType();
            Class<?> next = unwrapCollectionElementType(field.getGenericType(), raw);
            return new PropertyHit(field.getName(), next);
        }

        // 3) If it's a record, try record component accessor
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

        // getXxx()
        java.lang.reflect.Method m = findNoArgMethod(type, "get" + cap);
        if (m != null) {
            return m;
        }

        // isXxx() for boolean
        m = findNoArgMethod(type, "is" + cap);
        if (m != null && (m.getReturnType() == boolean.class || m.getReturnType() == Boolean.class)) {
            return m;
        }

        return null;
    }

    // Version: 2026-01-24
    private java.lang.reflect.Method findNoArgMethod(Class<?> type, String methodName) {

        // 1) Fast path: public method lookup (includes inherited)
        try {
            return type.getMethod(methodName);
        } catch (NoSuchMethodException ignored) {
            // fall through
        }

        // 2) Fallback: declared methods (package/private/protected) across hierarchy
        Class<?> t = type;
        while (t != null && t != Object.class) {
            try {
                java.lang.reflect.Method m = t.getDeclaredMethod(methodName);

                // DO NOT call m.canAccess(null) for instance methods.
                // If you truly need access, just attempt setAccessible and handle the failure.
                try {
                    m.setAccessible(true);
                } catch (RuntimeException ignored) {
                    // In Java 17, this may throw InaccessibleObjectException under JPMS.
                    // That's fine: caller can still try using the method if it's accessible.
                }

                return m;

            } catch (NoSuchMethodException ignored) {
                // continue
            }
            t = t.getSuperclass();
        }

        return null;
    }


    // Version: 2026-01-24
    private java.lang.reflect.Field findField(Class<?> type, String name) {

        // 1) Public field lookup (includes inherited)
        try {
            return type.getField(name);
        } catch (NoSuchFieldException ignored) {
            // fall through
        }

        // 2) Declared field lookup across hierarchy
        Class<?> t = type;
        while (t != null && t != Object.class) {
            try {
                java.lang.reflect.Field f = t.getDeclaredField(name);
                try {
                    f.setAccessible(true);
                } catch (RuntimeException ignored) {
                    // Java 17 module restrictions may block this.
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

        // If it's an array, element type is component type
        if (rawType.isArray()) {
            return rawType.getComponentType();
        }

        // If it's a Collection<T>, attempt to extract T from generic signature
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
            // No generic info available: we can’t know element type, so keep rawType
            return Object.class;
        }

        // Otherwise, plain scalar / complex type
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

    // ----------------------------
    // Supporting types
    // ----------------------------

    public interface ExcelAliasResolver {
        /**
         * Convert spreadsheet segment name to Java property name.
         *
         * Parameters give you context to do route/fact-specific rewrites if you want:
         * - rootType: the chosen fact root class
         * - parts/startIndex: the full path and where the tail begins
         * - segmentIndex: current segment index in parts
         * - currentType: the type we are currently reflecting on
         * - spreadsheetName: the raw name from the spreadsheet
         */
        String toJavaName(Class<?> rootType,
                          String[] parts,
                          int startIndex,
                          int segmentIndex,
                          Class<?> currentType,
                          String spreadsheetName);
    }

    public static final class NoOpExcelAliasResolver implements ExcelAliasResolver {
        @Override
        public String toJavaName(Class<?> rootType,
                                 String[] parts,
                                 int startIndex,
                                 int segmentIndex,
                                 Class<?> currentType,
                                 String spreadsheetName) {
            return spreadsheetName;
        }
    }

    public static final class PathResolution {
        private final String path;

        public PathResolution(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
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
