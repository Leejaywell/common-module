package pers.lee.common.lang.json.gson.converter;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Passyt on 2018/10/17.
 */
public class JavaLocalTimeTypeAdapter extends TypeAdapter<LocalTime> {

    public static final Type TYPE = new TypeToken<LocalTime>() {}.getType();

    private DateTimeFormatter formatter;

    public JavaLocalTimeTypeAdapter() {
        this(DateTimeFormatter.ISO_LOCAL_TIME);
    }

    public JavaLocalTimeTypeAdapter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    public void setFormat(DateTimeFormatter dateFormat) {
        this.formatter = dateFormat;
    }

    @Override
    public void write(JsonWriter out, LocalTime date) throws IOException {
        if (date == null) {
            out.nullValue();
        } else {
            out.value(formatter.format(date));
        }
    }

    @Override
    public LocalTime read(JsonReader in) throws IOException {
        switch (in.peek()) {
            case NULL:
                in.nextNull();
                return null;
            default:
                String date = in.nextString();
                return LocalTime.parse(date, formatter);
        }
    }
}