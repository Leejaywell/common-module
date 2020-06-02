package pers.lee.common.lang.json;

import pers.lee.common.lang.json.gson.GsonHandlerProvider;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public final class Json {

    private JsonHandlerProvider provider;

    private static Json DEFAULT_INSTANCE = new Json();

    public Json() {
        this.provider = new GsonHandlerProvider();
    }

    public Json(JsonHandlerProvider provider) {
        this.provider = provider;
    }

    public static Json getDefault() {
        return DEFAULT_INSTANCE;
    }

    public static void setDefaultProvider(JsonHandlerProvider provider) {
        DEFAULT_INSTANCE.provider = provider;
    }

    public JsonHandlerProvider getJsonHandlerProvider() {
        return provider;
    }

    public Object deserialize(Reader reader) throws IOException {
        return provider.getDeserializer().deserialize(reader);
    }

    public <T> T deserialize(Reader reader, Class<T> valueType) throws IOException {
        return provider.getDeserializer().deserialize(reader, valueType);
    }

    public <T> T deserialize(Reader reader, Type valueType) throws IOException {
        return provider.getDeserializer().deserialize(reader, valueType);
    }

    public void serialize(Writer writer, Object object) throws IOException {
        provider.getSerializer().serialize(writer, object);
    }

    public String toJsonString(Object object) {
        return provider.getSerializer().toJsonString(object);
    }

    public <T> T toJavaObject(Object object, Type valueType) {
        if (object == null) {
            return null;
        }
        String jsonString = toJsonString(object);
        try {
            return deserialize(new StringReader(jsonString), valueType);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("invalid json value " + jsonString, e);
        }
    }

    public <T> T toJavaObject(String jsonString, Type valueType) {
        if (jsonString == null || jsonString.length() == 0) {
            return null;
        }
        try {
            return deserialize(new StringReader(jsonString), valueType);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("invalid json value " + jsonString, e);
        }
    }

    public <T> List<T> toList(String jsonString, Class<T> valueType) {
        if (jsonString == null || jsonString.length() == 0) {
            return null;
        }
        try {
            return deserialize(new StringReader(jsonString), SimpleParameterizedType.createCollectionType(List.class, valueType));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("invalid json value " + jsonString, e);
        }
    }

    public <T> Set<T> toSet(String jsonString, Class<T> valueType) {
        if (jsonString == null || jsonString.length() == 0) {
            return null;
        }
        try {
            return deserialize(new StringReader(jsonString), SimpleParameterizedType.createCollectionType(Set.class, valueType));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("invalid json value " + jsonString, e);
        }
    }

    public <K, V> Map<K, V> toMap(String jsonString, Class<K> keyType, Class<V> valueType) {
        if (jsonString == null || jsonString.length() == 0) {
            return null;
        }
        try {
            return deserialize(new StringReader(jsonString), SimpleParameterizedType.createMapType(Map.class, keyType, valueType));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("invalid json value " + jsonString, e);
        }
    }

    public Object toJsonObject(Object object) {
        try {
            return provider.getSerializer().toJsonObject(object);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("write to json object error ", e);
        }
    }

    @SuppressWarnings("rawtypes")
    public static class SimpleParameterizedType implements ParameterizedType {

        private Class ownerType;
        private Type[] argumentTypes;

        public static SimpleParameterizedType createCollectionType(Class collectionClass, Class itemType) {
            SimpleParameterizedType simpleParameterizedType = new SimpleParameterizedType();
            simpleParameterizedType.ownerType = collectionClass;
            simpleParameterizedType.argumentTypes = new Type[]{itemType};

            return simpleParameterizedType;
        }

        public static SimpleParameterizedType createMapType(Class mapClass, Class keyType, Class valueType) {
            SimpleParameterizedType simpleParameterizedType = new SimpleParameterizedType();
            simpleParameterizedType.ownerType = mapClass;
            simpleParameterizedType.argumentTypes = new Type[]{keyType, valueType};

            return simpleParameterizedType;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return argumentTypes;
        }

        @Override
        public Type getRawType() {
            return ownerType;
        }

        @Override
        public Type getOwnerType() {
            return ownerType;
        }
    }

}
