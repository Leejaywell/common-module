package pers.lee.common.rpc.ci.convertor;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.hibernate.collection.spi.PersistentCollection;

import java.io.IOException;

/**
 * @author: Jay
 * @date: 2018/5/11
 */
public class HibernatePersistentCollection extends TypeAdapter<Iterable> {

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @Override
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            return (PersistentCollection.class.isAssignableFrom(type.getRawType()) ? (TypeAdapter<T>) new HibernatePersistentCollection(gson) : null);
        }
    };

    private final Gson context;

    private HibernatePersistentCollection(Gson context) {
        this.context = context;
    }

    @Override
    public Iterable read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException("Not supported");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void write(JsonWriter out, Iterable value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.beginArray();
        if (value instanceof PersistentCollection) {
            ((PersistentCollection) value).forceInitialization();
            for (Object o : value) {
                // Get the TypeAdapter of the original class, to delegate the serialization
                TypeAdapter delegate = context.getAdapter(o.getClass());
                delegate.write(out, o);
            }
        }
        out.endArray();
    }

}
