package pers.lee.common.lang.json.gson.converter;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Passyt on 2018/10/16.
 */
public class JavaZonedDateTimeTypeAdapter extends TypeAdapter<ZonedDateTime> {

    private static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static final Type TYPE = new TypeToken<ZonedDateTime>() {}.getType();

    @Override
    public void write(JsonWriter out, ZonedDateTime date) throws IOException {
        if (date == null) {
            out.nullValue();
        } else {
//            out.value(DateTimeFormatter.ISO_INSTANT.format(date));
            out.value(DATE_TIME_FORMATTER.format(date.withZoneSameInstant(ZoneOffset.UTC)));
        }
    }

    @Override
    public ZonedDateTime read(JsonReader in) throws IOException {
        switch (in.peek()) {
            case NULL:
                in.nextNull();
                return null;
            default:
                String date = in.nextString();
                return ZonedDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
    }
}
