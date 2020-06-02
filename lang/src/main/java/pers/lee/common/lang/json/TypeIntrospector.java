package pers.lee.common.lang.json;

import pers.lee.common.lang.utils.ReflectUtils;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author YangYang
 * @version 0.1, 2008-7-8 10:08:44
 */
@SuppressWarnings("unchecked")
public class TypeIntrospector {

    public static final String TYPE_NUMBER = "number";
    public static final String TYPE_STRING = "string";
    public static final String TYPE_BOOLEAN = "boolean";
    public static final String TYPE_ARRAY = "array";
    public static final String TYPE_STRUCT = "struct";
    public static final String TYPE_ANY = "any";
    public static final String TYPE_UNKNOWN = "unknown";

    private List<Class> classPool = new ArrayList<Class>();

    public Object introspect(Type type) {
        if (type instanceof ParameterizedType) {
            return introspectParameterizedType(type);
        } else if (type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) type;
            List arrayType = new ArrayList();
            arrayType.add(introspect(genericArrayType.getGenericComponentType()));
            return arrayType;
        } else if (type instanceof Class) {
            Class clazz = (Class) type;
            if (clazz.isArray()) {
                List arrayType = new ArrayList();
                arrayType.add(introspect(clazz.getComponentType()));
                return arrayType;
            }
            if (String.class.equals(clazz)) {
                return TYPE_STRING;
            }
            if (Boolean.class.equals(clazz)) {
                return TYPE_BOOLEAN;
            }
            if (Date.class.equals(clazz)) {
                Map structType = new HashMap();
                structType.put("time", TYPE_NUMBER);
                return structType;
            }
            if (Object.class.equals(clazz)) {
                return TYPE_ANY;
            }

            if (Integer.TYPE.equals(clazz) || Double.TYPE.equals(clazz) || Float.TYPE.equals(clazz) || Long.TYPE.equals(clazz)
                    || Short.TYPE.equals(clazz) || Byte.TYPE.equals(clazz)) {
                return TYPE_NUMBER;
            }

            Set<Class> interfaces = ReflectUtils.getInterfaces(clazz);
            if (interfaces.contains(Collection.class)) {
                List arrayType = new ArrayList();
                arrayType.add(TYPE_ANY);
                return arrayType;
            }

            if (interfaces.contains(Map.class)) {
                Map structType = new HashMap();
                structType.put(TYPE_STRING, TYPE_ANY);
                return structType;
            }

            Set<Class> superClasses = ReflectUtils.getSuperClasses(clazz);
            if (superClasses.contains(Number.class)) {
                return TYPE_NUMBER;
            }

            if (superClasses.contains(Enum.class)) {
                return TYPE_STRING;
            }
            if (isPooled(clazz)) {
                return null;
            }
            classPool.add(clazz);

            PropertyIntrospectProcessor propertyIntrospectProcessor = new PropertyIntrospectProcessor();
            ReflectUtils.forIn(clazz, propertyIntrospectProcessor);
            classPool.remove(classPool.size() - 1);
            return propertyIntrospectProcessor.getType();
        }
        return TYPE_UNKNOWN;
    }

    private boolean isPooled(Class clazz) {
        return classPool.contains(clazz);
    }

    private class PropertyIntrospectProcessor implements ReflectUtils.PropertyProcessor {
        private HashMap type = new HashMap();

        public HashMap getType() {
            return type;
        }

        public void process(ReflectUtils.BeanProperty beanProperty) {
            if ("class".equals(beanProperty.getPropertyName())) {
                return;
            }
            Object propertyType = introspect(beanProperty.getPropertyType());
            if (propertyType != null) {
                type.put(beanProperty.getPropertyName(), propertyType);
            }
        }
    }

    private Object introspectParameterizedType(Type type) {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Class clazz = (Class) parameterizedType.getRawType();
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Set<Class> interfaces = ReflectUtils.getInterfaces(clazz);
        if (clazz.isInterface()) {
            interfaces.add(clazz);
        }
        if (interfaces.contains(Collection.class) && actualTypeArguments.length == 1) {
            List arrayType = new ArrayList();
            arrayType.add(introspect(actualTypeArguments[0]));
            return arrayType;
        } else if (interfaces.contains(Map.class) && actualTypeArguments.length == 2
                && String.class.equals(actualTypeArguments[0])) {
            Map structType = new HashMap();
            Object propertyType = introspect(actualTypeArguments[1]);
            if (propertyType != null) {
                structType.put(TYPE_STRING, propertyType);
            }
            return structType;
        } else {
            return TYPE_UNKNOWN;
        }
    }
}
