package uk.gov.hmrc.rules.br455.reflection;

public final class ReflectionScalarPathResolver {

    // Version: 2026-01-23 v1.0.0

    private final ExcelAliasResolver aliasResolver;

    public ReflectionScalarPathResolver(ExcelAliasResolver aliasResolver) {
        this.aliasResolver = aliasResolver;
    }

    /**
     * Always returns a scalar leaf path suitable for Drools comparisons.
     *
     * Rules:
     * - Walk tokens using reflection (fields/getters).
     * - If a token doesn't exist, try aliasResolver.translateToken(currentType, token) and retry.
     * - If trailing ".code" exists:
     *    - Prefer a real "code" property on the nested type (rare)
     *    - Otherwise rewrite previous token to "<prevToken>Code" on the parent type (common)
     * - If token doesn't exist and no alias helps, also try flattened "<token>Code" on current type (for "type.code" style).
     */
    public ResolvedScalarPath resolveScalar(Class<?> rootType, String excelPath) {
        if (rootType == null) {
            throw new IllegalArgumentException("rootType must not be null");
        }
        if (excelPath == null || excelPath.isBlank()) {
            throw new IllegalArgumentException("excelPath must not be blank");
        }

        String[] raw = excelPath.trim().split("\\.");
        boolean endsWithCode = raw.length > 1 && "code".equalsIgnoreCase(raw[raw.length - 1]);

        // We will walk all tokens except a trailing "code" (handled specially).
        int walkLen = endsWithCode ? raw.length - 1 : raw.length;

        Class<?> currentType = rootType;
        StringBuilder outPath = new StringBuilder();

        // We need to remember the "parent hop" so we can do X.code -> XCode on the parent if needed.
        Class<?> previousType = null;
        String previousResolvedProp = null; // the Java property name we actually used for the previous token
        ReflectedProperty previousPropMeta = null;

        for (int i = 0; i < walkLen; i++) {
            String token = raw[i];

            // Resolve this token on currentType (direct -> alias -> flattenedCode)
            ReflectedProperty prop = resolvePropertyWithFallbacks(currentType, token);

            // Append resolved property name
            if (outPath.length() > 0) outPath.append('.');
            outPath.append(prop.propertyName);

            // If this property is scalar, we're done (cannot go deeper).
            if (isScalarType(prop.rawType)) {
                // If there are still tokens left (excluding trailing .code), that's invalid.
                if (i < walkLen - 1) {
                    throw fail("Path continues after scalar",
                            rootType, excelPath, currentType, token, prop.propertyName);
                }
                previousType = currentType;
                previousResolvedProp = prop.propertyName;
                previousPropMeta = prop;
                currentType = prop.rawType;
                break;
            }

            // Complex: hop to nested type for next token
            previousType = currentType;
            previousResolvedProp = prop.propertyName;
            previousPropMeta = prop;

            currentType = determineNextType(prop);
        }

        // Handle trailing ".code" (if present)
        if (endsWithCode) {
            if (previousResolvedProp == null || previousType == null) {
                throw fail("Trailing .code but no previous token resolved",
                        rootType, excelPath, currentType, "code", null);
            }

            // Case 1: real nested "code" property exists on the CURRENT nested type (rare)
            ReflectedProperty codeProp = findProperty(currentType, "code");
            if (codeProp != null && isScalarType(codeProp.rawType)) {
                outPath.append('.').append(codeProp.propertyName);
                return new ResolvedScalarPath(outPath.toString(), "navigatedToRealCodeProperty");
            }

            // Case 2: flattened "<previous>Code" exists on the PARENT type (common)
            String flattenedOnParent = previousResolvedProp + "Code";
            ReflectedProperty parentFlattenedProp = findProperty(previousType, flattenedOnParent);

            if (parentFlattenedProp != null && isScalarType(parentFlattenedProp.rawType)) {
                // Replace the last segment in outPath with flattened field name.
                replaceLastSegment(outPath, parentFlattenedProp.propertyName);
                return new ResolvedScalarPath(outPath.toString(), "flattenedCodeOnParent (" + previousResolvedProp + ".code -> " + parentFlattenedProp.propertyName + ")");
            }

            // Case 3: sometimes the previous token itself was an alias; try alias-based flattened too
            // (e.g., excel "investmentType.code" -> alias "type" -> "typeCode")
            // previousResolvedProp is already the Java name, so we already did the right thing above.
            // If it didn't exist, then truly unresolved.
            throw fail("Trailing .code could not be resolved as real 'code' or flattened '<prev>Code' on parent",
                    rootType, excelPath, previousType, previousResolvedProp + ".code", null);
        }

        // No trailing .code: we must ensure final path ends in scalar, otherwise it's not usable for scalar comparisons.
        if (previousPropMeta == null) {
            throw fail("No tokens resolved (unexpected)", rootType, excelPath, rootType, excelPath, null);
        }
        // If the last resolved prop was complex, we ended on an object path without a scalar leaf.
        // That would be unusable for scalar-only rules.
        if (!isScalarType(previousPropMeta.rawType)) {
            throw fail("Resolved path ends on a complex object but scalar is required",
                    rootType, excelPath, previousType, previousResolvedProp, outPath.toString());
        }

        return new ResolvedScalarPath(outPath.toString(), "directScalar");
    }

    private ReflectedProperty resolvePropertyWithFallbacks(Class<?> currentType, String excelToken) {
        // 1) direct token / lowerFirst(token)
        ReflectedProperty p = findProperty(currentType, excelToken);
        if (p != null) return p;

        String lowered = lowerFirst(excelToken);
        if (!lowered.equals(excelToken)) {
            p = findProperty(currentType, lowered);
            if (p != null) return p;
        }

        // 2) alias translation (single token)
        String alias = aliasResolver == null ? null : aliasResolver.translateToken(currentType, excelToken);
        if (alias != null && !alias.isBlank()) {
            ReflectedProperty aliasProp = findProperty(currentType, alias);
            if (aliasProp != null) return aliasProp;

            String aliasLowered = lowerFirst(alias);
            if (!aliasLowered.equals(alias)) {
                aliasProp = findProperty(currentType, aliasLowered);
                if (aliasProp != null) return aliasProp;
            }
        }

        // 3) flattened "<token>Code" on currentType (handles "type.code" where "type" isn't a real field)
        // Note: we do NOT require excelToken to end with .code here because in practice you may be resolving
        // the "type" segment before seeing the ".code" segment.
        String flattened = lowerFirst(excelToken) + "Code";
        ReflectedProperty flattenedProp = findProperty(currentType, flattened);
        if (flattenedProp != null) return flattenedProp;

        // 4) alias-based flattened
        if (alias != null && !alias.isBlank()) {
            String aliasFlattened = lowerFirst(alias) + "Code";
            ReflectedProperty aliasFlattenedProp = findProperty(currentType, aliasFlattened);
            if (aliasFlattenedProp != null) return aliasFlattenedProp;
        }

        throw fail("Unresolved token",
                currentType, excelToken);
    }

    private RuntimeException fail(String msg, Class<?> rootType, String excelPath, Class<?> currentType, String token, String extra) {
        String details = msg
                + " | rootType=" + rootType.getName()
                + " | excelPath='" + excelPath + "'"
                + " | currentType=" + (currentType == null ? "null" : currentType.getName())
                + " | token='" + token + "'"
                + (extra == null ? "" : " | extra=" + extra)
                + " | available=" + describeProperties(currentType);
        return new IllegalStateException(details);
    }

    private RuntimeException fail(String msg, Class<?> currentType, String token) {
        String details = msg
                + " | currentType=" + (currentType == null ? "null" : currentType.getName())
                + " | token='" + token + "'"
                + " | available=" + describeProperties(currentType);
        return new IllegalStateException(details);
    }

    private void replaceLastSegment(StringBuilder path, String newLast) {
        int lastDot = path.lastIndexOf(".");
        if (lastDot < 0) {
            path.setLength(0);
            path.append(newLast);
            return;
        }
        path.setLength(lastDot + 1);
        path.append(newLast);
    }

    private Class<?> determineNextType(ReflectedProperty p) {
        // If it's a collection, try to use generic element type; else use raw type.
        if (p.isCollection) {
            Class<?> element = p.collectionElementTypeOrNull();
            return element != null ? element : Object.class;
        }
        return p.rawType;
    }

    private ReflectedProperty findProperty(Class<?> type, String propertyName) {
        if (type == null || propertyName == null || propertyName.isBlank()) return null;

        java.lang.reflect.Field f = findField(type, propertyName);
        if (f != null) {
            return ReflectedProperty.fromField(f);
        }

        java.lang.reflect.Method m = findGetter(type, propertyName);
        if (m != null) {
            return ReflectedProperty.fromGetter(propertyName, m);
        }

        return null;
    }

    private java.lang.reflect.Field findField(Class<?> type, String fieldName) {
        Class<?> t = type;
        while (t != null && t != Object.class) {
            try {
                java.lang.reflect.Field f = t.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException ignored) {
                t = t.getSuperclass();
            }
        }
        return null;
    }

    private java.lang.reflect.Method findGetter(Class<?> type, String propertyName) {
        String cap = Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        String getName = "get" + cap;
        String isName = "is" + cap;

        for (java.lang.reflect.Method m : type.getMethods()) {
            if (m.getParameterCount() != 0) continue;
            if (m.getName().equals(getName) || m.getName().equals(isName)) {
                return m;
            }
        }
        return null;
    }

    private String lowerFirst(String s) {
        if (s == null || s.isEmpty()) return s;
        char c0 = s.charAt(0);
        if (Character.isLowerCase(c0)) return s;
        return Character.toLowerCase(c0) + s.substring(1);
    }

    private boolean isScalarType(Class<?> t) {
        if (t == null) return false;
        if (t.isPrimitive()) return true;
        if (t == String.class) return true;
        if (Number.class.isAssignableFrom(t)) return true;
        if (t == Boolean.class || t == Character.class) return true;
        if (t.isEnum()) return true;
        if (t == java.math.BigDecimal.class || t == java.math.BigInteger.class) return true;
        if (t == java.util.UUID.class) return true;
        if (t.getName().startsWith("java.time.")) return true;
        return false;
    }

    private String describeProperties(Class<?> type) {
        if (type == null) return "n/a";

        java.util.Set<String> names = new java.util.TreeSet<>();

        Class<?> t = type;
        while (t != null && t != Object.class) {
            for (java.lang.reflect.Field f : t.getDeclaredFields()) {
                names.add(f.getName());
            }
            t = t.getSuperclass();
        }

        for (java.lang.reflect.Method m : type.getMethods()) {
            if (m.getParameterCount() != 0) continue;
            String n = m.getName();
            if (n.startsWith("get") && n.length() > 3) {
                names.add(lowerFirst(n.substring(3)));
            } else if (n.startsWith("is") && n.length() > 2) {
                names.add(lowerFirst(n.substring(2)));
            }
        }

        return String.join(", ", names);
    }

    private static final class ReflectedProperty {
        private final String propertyName;
        private final Class<?> rawType;
        private final java.lang.reflect.Type genericType;
        private final boolean isCollection;

        private ReflectedProperty(String propertyName, Class<?> rawType, java.lang.reflect.Type genericType) {
            this.propertyName = propertyName;
            this.rawType = rawType;
            this.genericType = genericType;
            this.isCollection = java.util.Collection.class.isAssignableFrom(rawType) || rawType.isArray();
        }

        static ReflectedProperty fromField(java.lang.reflect.Field f) {
            return new ReflectedProperty(f.getName(), f.getType(), f.getGenericType());
        }

        static ReflectedProperty fromGetter(String propertyName, java.lang.reflect.Method m) {
            return new ReflectedProperty(propertyName, m.getReturnType(), m.getGenericReturnType());
        }

        Class<?> collectionElementTypeOrNull() {
            if (!isCollection) return null;
            if (rawType.isArray()) return rawType.getComponentType();

            if (genericType instanceof java.lang.reflect.ParameterizedType pt) {
                java.lang.reflect.Type[] args = pt.getActualTypeArguments();
                if (args.length == 1) {
                    java.lang.reflect.Type a = args[0];
                    if (a instanceof Class<?> c) return c;
                    if (a instanceof java.lang.reflect.ParameterizedType ptt && ptt.getRawType() instanceof Class<?> rc) return rc;
                }
            }
            return null;
        }
    }
}
