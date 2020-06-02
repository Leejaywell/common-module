package pers.lee.common.lang.json;

import java.io.StringWriter;
import java.io.Writer;

public interface JsonSerializer {

    /**
     * @param writer
     * @param object
     */
    void serialize(Writer writer, Object object);

    /**
     * @param object
     * @return
     */
    Object toJsonObject(Object object);

    /**
     * @param object
     * @return
     */
    default String toJsonString(Object object) {
        StringWriter writer = new StringWriter();
        serialize(writer, object);
        return writer.toString();
    }

}
