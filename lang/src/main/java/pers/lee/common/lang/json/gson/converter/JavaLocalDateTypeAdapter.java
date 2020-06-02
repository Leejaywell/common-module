package pers.lee.common.lang.json.gson.converter;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by Passyt on 2018/10/16.
 */
public class JavaLocalDateTypeAdapter extends TypeAdapter<LocalDate> {

    public static final Type TYPE = new TypeToken<LocalDate>() {}.getType();

    private DateTimeFormatter formatter;

    public JavaLocalDateTypeAdapter() {
        this(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public JavaLocalDateTypeAdapter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    public void setFormat(DateTimeFormatter dateFormat) {
        this.formatter = dateFormat;
    }

    @Override
    public void write(JsonWriter out, LocalDate date) throws IOException {
        if (date == null) {
            out.nullValue();
        } else {
            out.value(formatter.format(date));
        }
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        switch (in.peek()) {
            case NULL:
                in.nextNull();
                return null;
            default:
                String date = in.nextString();
                return LocalDate.parse(date, formatter);
        }
    }
}
