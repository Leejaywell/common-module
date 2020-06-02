package pers.lee.common.lang.json.gson.converter;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Passyt on 2018/10/16.
 */
public class JavaOffsetDateTimeTypeAdapter extends TypeAdapter<OffsetDateTime> {

    public static final Type TYPE = new TypeToken<OffsetDateTime>() {}.getType();

    private DateTimeFormatter formatter;

    public JavaOffsetDateTimeTypeAdapter() {
        this(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public JavaOffsetDateTimeTypeAdapter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    public void setFormat(DateTimeFormatter dateFormat) {
        this.formatter = dateFormat;
    }

    @Override
    public void write(JsonWriter out, OffsetDateTime date) throws IOException {
        if (date == null) {
            out.nullValue();
        } else {
            out.value(formatter.format(date));
        }
    }

    @Override
    public OffsetDateTime read(JsonReader in) throws IOException {
        switch (in.peek()) {
            case NULL:
                in.nextNull();
                return null;
            default:
                String date = in.nextString();
                return OffsetDateTime.parse(date, formatter);
        }
    }
}