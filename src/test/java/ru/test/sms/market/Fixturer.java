package ru.test.sms.market;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class that can be used as concrete class object creator.
 * Creates object of specified class and initializes it fields.
 * Object creation process can be customized.
 * <p>
 * Known bugs: doesn't initializes newsted classes. credit -> RepaymentOrderServiceTest#shouldReturnSuccessResponseFromEsbCreate
 *
 * @param <T> class of object to be created
 */
public final class Fixturer<T> {

    private static final Map<Class, Supplier<?>> SUPPLIERS = new HashMap<>();
    private static final String[] STRING_POOL = {
            "this", "is", " ", "a",
            "test", "string", "to",
            "form", "randomized", "string",
            "such", "a", "godlike",
            "code", "*", "#",
            "-", "+", "?"
    };

    static {
        Random random = new Random();

        SUPPLIERS.put(LocalDate.class, LocalDate::now);
        SUPPLIERS.put(Instant.class, Instant::now);
        SUPPLIERS.put(Timestamp.class, () -> Timestamp.valueOf(LocalDateTime.now()));
        SUPPLIERS.put(ZonedDateTime.class, ZonedDateTime::now);

        SUPPLIERS.put(Long.class, random::nextLong);
        SUPPLIERS.put(Integer.class, random::nextInt);
        SUPPLIERS.put(Short.class, () -> (short) random.nextInt());
        SUPPLIERS.put(Byte.class, () -> (byte) random.nextInt());
        SUPPLIERS.put(long.class, random::nextLong);
        SUPPLIERS.put(int.class, random::nextInt);
        SUPPLIERS.put(short.class, () -> (short) random.nextInt());
        SUPPLIERS.put(byte.class, () -> (byte) random.nextInt());

        SUPPLIERS.put(Double.class, random::nextDouble);
        SUPPLIERS.put(Float.class, random::nextFloat);
        SUPPLIERS.put(double.class, random::nextDouble);
        SUPPLIERS.put(float.class, random::nextFloat);

        SUPPLIERS.put(String.class, () -> {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 1 + random.nextInt(4); i++) {
                builder.append(STRING_POOL[random.nextInt(STRING_POOL.length)]);
            }
            return builder.toString();
        });
        SUPPLIERS.put(Character.class, () -> (char) (random.nextInt(26) + 'a'));
        SUPPLIERS.put(char.class, () -> (char) (random.nextInt(26) + 'a'));

        SUPPLIERS.put(Boolean.class, random::nextBoolean);
        SUPPLIERS.put(boolean.class, random::nextBoolean);

        SUPPLIERS.put(List.class, Collections::emptyList);
        SUPPLIERS.put(Set.class, Collections::emptySet);
        SUPPLIERS.put(Map.class, Collections::emptyMap);

        SUPPLIERS.put(UUID.class, UUID::randomUUID);
        SUPPLIERS.put(Date.class, Date::new);

        SUPPLIERS.put(BigDecimal.class, () -> BigDecimal.valueOf(random.nextInt()));
    }

    private Consumer<? super T> stateActionHolder;
    private Class<? extends T> clazz;
    private boolean initCollections;
    private boolean initInherited;
    private boolean initSuperclassPrivateFields;
    private int maxLen = 0;

    private Fixturer(Class<? extends T> clazz) {
        this.clazz = clazz;
    }

    private Fixturer(Class<? extends T> clazz, Consumer<? super T> stateActionHolder) {
        this.clazz = clazz;
        this.stateActionHolder = stateActionHolder;
    }

    /**
     * Create Fixturer instance with pre populated default
     * key - value field property map.
     *
     * @param clazz class to be created by fixturer
     * @return Fixturer instance
     */
    public static <T> Fixturer<T> concreteFixturer(Class<? extends T> clazz) {
        return new Fixturer<>(clazz);
    }

    /**
     * Create Fixturer instance with pre populated default
     * key - value field property map and state holder action.
     * Use it as concrete entity factory
     *
     * @param clazz             class to be created by fixturer
     * @param stateActionHolder custom instantiation logic that will be applied
     *                          every time on object instantiation. Performed
     *                          before callback.
     * @return Fixturer instance
     */
    public static <T> Fixturer<T> concreteFixturer(Class<? extends T> clazz, Consumer<? super T> stateActionHolder) {
        return new Fixturer<>(clazz, stateActionHolder);
    }

    /**
     * Creates a fixture object with default valued fields
     *
     * @param clazz class to be fixtured
     * @return fixture object of class
     * @throws RuntimeException if can't create object or set field
     */
    public static <T> T create(Class<? extends T> clazz) {
        return staticFixture(clazz, null);
    }

    /**
     * Creates a fixture object with default valued fields
     * and performs an action on created object. Use it
     * to set domain specific fields or fields to be tested.
     *
     * @param clazz    class to be fixtured
     * @param callback action to be performed
     * @return fixture object of class
     * @throws RuntimeException if can't create object or set field
     */
    public static <T> T create(Class<? extends T> clazz, Consumer<? super T> callback) {
        return staticFixture(clazz, callback);
    }

    /**
     * Get fixture builder with several options available
     *
     * @param clazz class to be fixtured
     * @return fixture builder
     * @throws RuntimeException if can't create object or set field
     */
    public static <T> FixtureBuilder<T> builder(Class<? extends T> clazz) {
        return new FixtureBuilder<>(clazz);
    }

    private static <T> T staticFixture(Class<? extends T> clazz, Consumer<? super T> callback) {
        return new FixtureBuilder<T>(clazz)
                .initCollections(false)
                .initInheritedFields(false)
                .initSuperclassPrivateFields(false)
                .callback(callback)
                .build();
    }

    /**
     * Specify if inherited fields should be instantiated
     *
     * @param initInherited flag
     */
    public Fixturer<T> initInherited(boolean initInherited) {
        this.initInherited = initInherited;
        return this;
    }

    public Fixturer<T> initCollections(boolean initCollections) {
        this.initCollections = initCollections;
        return this;
    }

    /**
     * Specify if superclass private fields should be instantiated
     *
     * @param initSuperclassPrivateFields flag
     */
    public Fixturer<T> initSuperclassPrivateFields(boolean initSuperclassPrivateFields) {
        this.initSuperclassPrivateFields = initSuperclassPrivateFields;
        return this;
    }


    /**
     * Creates a fixture object with provided value fields pre populated
     * with defaults.
     *
     * @return fixture object of class
     * @throws RuntimeException if can't create object or set field
     */
    public T create() {
        return stateFixture(null);
    }

    public List<T> create(int capacity) {
        List<T> array = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            array.add(stateFixture(null));
        }
        return array;
    }

    /**
     * Creates a fixture object with provided value fields pre populated
     * with defaults, and performs an action on created object. Use it
     * to set domain specific fields or fields to be tested.
     *
     * @param callback action to be performed. Performed before state action.
     * @return fixture object of class
     * @throws RuntimeException if can't create object or set field
     */
    public T create(Consumer<? super T> callback) {
        return stateFixture(callback);
    }

    @SuppressWarnings("unchecked")
    public Fixturer<T> mixInit(Consumer<? super T> mixture) {
        if (stateActionHolder != null) {
            stateActionHolder = ((Consumer<T>) stateActionHolder).andThen(mixture);
        } else {
            this.stateActionHolder = mixture;
        }
        return this;
    }

    public Fixturer<T> maxLen(int maxLen) {
        this.maxLen = maxLen;
        return this;
    }

    private T stateFixture(Consumer<? super T> callback) {
        return new FixtureBuilder<T>(clazz)
                .initCollections(initCollections)
                .initInheritedFields(initInherited)
                .initSuperclassPrivateFields(initSuperclassPrivateFields)
                .callback(callback)
                .stateAction(stateActionHolder)
                .maxLen(maxLen)
                .build();
    }

    public static class FixtureBuilder<T> {
        private boolean initCollections;
        private boolean initInheritedFields;
        private boolean initSuperclassPrivateFields;
        private Class<? extends T> clazz;
        private Consumer<? super T> callback;
        private Consumer<? super T> stateAction;
        private int maxLen = 0;

        private FixtureBuilder(Class<? extends T> clazz) {
            this.clazz = clazz;
        }

        public FixtureBuilder<T> maxLen(int maxLen) {
            this.maxLen = maxLen;
            return this;
        }

        public FixtureBuilder<T> initCollections(boolean initCollections) {
            this.initCollections = initCollections;
            return this;
        }

        public FixtureBuilder<T> initSuperclassPrivateFields(boolean initSuperclassPrivateFields) {
            this.initSuperclassPrivateFields = initSuperclassPrivateFields;
            return this;
        }

        public FixtureBuilder<T> initInheritedFields(boolean initInheritedFields) {
            this.initInheritedFields = initInheritedFields;
            return this;
        }

        public FixtureBuilder<T> callback(Consumer<? super T> callback) {
            this.callback = callback;
            return this;
        }

        public FixtureBuilder<T> stateAction(Consumer<? super T> stateAction) {
            this.stateAction = stateAction;
            return this;
        }

        public T build() {
            T fixture = fillFixture(clazz);
            if (stateAction != null) {
                stateAction.accept(fixture);
            }
            if (callback != null) {
                callback.accept(fixture);
            }
            return fixture;
        }

        private T fillFixture(Class<? extends T> clazz) {
            T obj = instantiate(clazz);
            Field[] fields;
            if (initInheritedFields || initSuperclassPrivateFields) {
                fields = getSuperclassMergedFields(clazz);
            } else {
                fields = clazz.getDeclaredFields();
            }
            for (Field field : fields) {
                Class<?> fieldType = field.getType();
//                if ((!fieldType.isEnum() && !SUPPLIERS.containsKey(fieldType)) || Modifier.isFinal(field.getModifiers())) {
                if ((!fieldType.isEnum() && !SUPPLIERS.containsKey(fieldType)) || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                boolean instantiableCollection = Collection.class.isAssignableFrom(fieldType) | Map.class.isAssignableFrom(fieldType);
                try {
                    if (instantiableCollection & initCollections) {
                        field.set(obj, SUPPLIERS.get(fieldType).get());
                    } else if (fieldType.isEnum()) {
                        field.set(obj, fieldType.getEnumConstants()[0]);
                    } else if (!instantiableCollection) {
                        Object value = SUPPLIERS.get(fieldType).get();
                        if (maxLen > 0 && fieldType == String.class) {
                            String v = (String) value;
                            value = v.substring(0, maxLen < v.length() ? maxLen : v.length());
                        }
                        field.set(obj, value);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(String.format("Cant fixture %s", clazz), e);
                }

            }

            return obj;
        }

        private Field[] getSuperclassMergedFields(Class<?> clazz) {
            Class<?> superClass;
            List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
            while ((superClass = clazz.getSuperclass()) != null) {
                List<Field> inheritedFields = Arrays.stream(superClass.getDeclaredFields())
                        .filter((f) -> Modifier.isPublic(f.getModifiers())
                                || Modifier.isProtected(f.getModifiers())
                                || (initSuperclassPrivateFields && Modifier.isPrivate(f.getModifiers())))
                        .collect(Collectors.toList());
                fields.addAll(inheritedFields);
                clazz = superClass;
            }
            return fields.toArray(new Field[fields.size()]);
        }

        @SuppressWarnings("unchecked")
        private T instantiate(Class<? extends T> clazz) {
            T instance = null;
            Constructor[] constructors = clazz.getConstructors();
            Arrays.sort(constructors, Comparator.comparing(Constructor::getParameterCount));
            for (Constructor constructor : constructors) {
                Parameter[] parameters = constructor.getParameters();
                try {
                    if (parameters.length == 0) {
                        instance = (T) constructor.newInstance();
                        break;
                    } else if (canInstantiate(parameters)) {
                        instance = (T) constructor.newInstance(Stream.of(parameters).map(p -> {
                            Class<?> paramClass = p.getType();
                            if (paramClass.isEnum()) {
                                return paramClass.getEnumConstants()[0];
                            } else {
                                return SUPPLIERS.get(paramClass).get();
                            }
                        }).toArray());
                        break;
                    }
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    throw new RuntimeException(String.format("Cant fixture %s", clazz), e);
                }

            }
            if (instance == null) {
                throw new RuntimeException(String.format("Can not instantiate class. It has no default " +
                                "constructor or constructor is with unsupported " +
                                "parameters.\nDefaults:\njava.lang.Enum\n%s",
                        SUPPLIERS.keySet().stream().map(Class::toString)
                                .collect(Collectors.joining("\n"))));
            }
            return instance;
        }

        private boolean canInstantiate(Parameter[] parameters) {
            for (Parameter parameter : parameters) {
                if (!SUPPLIERS.containsKey(parameter.getType()) & !parameter.getType().isEnum()) {
                    return false;
                }
            }
            return true;

        }
    }

}

