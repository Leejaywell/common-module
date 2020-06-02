package pers.lee.common.lang.json.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import pers.lee.common.lang.json.JsonDeserializer;
import pers.lee.common.lang.json.JsonHandlerProvider;
import pers.lee.common.lang.json.JsonSerializer;
import pers.lee.common.lang.json.gson.converter.*;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Jay
 * @date: 2018/4/19
 */
public class GsonHandlerProvider implements JsonHandlerProvider {

    private final GsonDeserializer deserializer;
    private final GsonSerializer serializer;

    private static Map<Type, Object> REGISTER_TYPE_ADAPTERS = new HashMap<>();

    public GsonHandlerProvider() {
        this(defaultGsonBuilder().create());
    }

    public GsonHandlerProvider(Gson gson) {
        this.deserializer = new GsonDeserializer(gson);
        this.serializer = new GsonSerializer(gson);
    }

    @Override
    public JsonDeserializer getDeserializer() {
        return this.deserializer;
    }

    @Override
    public JsonSerializer getSerializer() {
        return this.serializer;
    }

    public static GsonBuilder defaultGsonBuilder() {
        GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapter(LocalTimeConverter.TYPE, new LocalTimeConverter())
                .registerTypeAdapter(LocalDateConverter.TYPE, new LocalDateConverter())
                .registerTypeAdapter(LocalDateTimeConverter.TYPE, new LocalDateTimeConverter())
                .registerTypeAdapter(DateTimeConverter.TYPE, new DateTimeConverter())
                .registerTypeAdapter(JavaDateTypeAdapter.TYPE, new JavaDateTypeAdapter())
                .registerTypeAdapter(JavaLocalTimeTypeAdapter.TYPE, new JavaLocalTimeTypeAdapter())
                .registerTypeAdapter(JavaLocalDateTypeAdapter.TYPE, new JavaLocalDateTypeAdapter())
                .registerTypeAdapter(JavaLocalDateTimeTypeConverter.TYPE, new JavaLocalDateTimeTypeConverter())
                .registerTypeAdapter(JavaOffsetDateTimeTypeAdapter.TYPE, new JavaOffsetDateTimeTypeAdapter())
                .registerTypeAdapter(JavaZonedDateTimeTypeAdapter.TYPE, new JavaZonedDateTimeTypeAdapter())
                .addSerializationExclusionStrategy(new CircularReferenceExclusionStrategy())
                .addDeserializationExclusionStrategy(new CircularReferenceExclusionStrategy())
                .enableComplexMapKeySerialization();
        REGISTER_TYPE_ADAPTERS.forEach((type, o) -> builder.registerTypeAdapter(type, o));
        return builder;
    }

    public static void registerTypeAdapter(Type type, Object adapter) {
        REGISTER_TYPE_ADAPTERS.put(type, adapter);
    }

    static class GsonDeserializer implements JsonDeserializer {

        private Gson gson;

        public GsonDeserializer(Gson gson) {
            this.gson = gson;
        }

        @Override
        public Object deserialize(Reader reader) {
            return gson.fromJson(reader, Object.class);
        }

        @Override
        public <T> T deserialize(Reader reader, Type valueType) {
            return gson.fromJson(reader, valueType);
        }
    }

    static class GsonSerializer implements JsonSerializer {

        private Gson gson;

        public GsonSerializer(Gson gson) {
            this.gson = gson;
        }

        @Override
        public void serialize(Writer writer, Object object) {
            gson.toJson(object, writer);
        }

        @Override
        public Object toJsonObject(Object object) {
            JsonElement jsonElement = gson.toJsonTree(object);
            if (jsonElement.isJsonNull()) {
                return null;
            } else if (jsonElement.isJsonPrimitive()) {
                return jsonElement.getAsString();
            } else if (jsonElement.isJsonArray()) {
                return gson.fromJson(jsonElement, List.class);
            } else if (jsonElement.isJsonObject()) {
                return gson.fromJson(jsonElement, Map.class);
            }
            return null;
        }
    }
}
