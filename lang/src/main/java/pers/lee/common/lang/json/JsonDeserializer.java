package pers.lee.common.lang.json;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

public interface JsonDeserializer {

    /**
     * @param reader
     * @return
     * @throws IOException
     */
    Object deserialize(Reader reader) throws IOException;

    /**
     * @param reader
     * @param valueType
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> T deserialize(Reader reader, Type valueType) throws IOException;
}
