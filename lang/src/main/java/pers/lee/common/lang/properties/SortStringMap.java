package pers.lee.common.lang.properties;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * SortStringMap
 *
 * @author Drizzt Yang
 */
public class SortStringMap extends HashMap<String, String> {

    public void read(InputStream inputStream, String charset) throws IOException {
        this.read(new InputStreamReader(inputStream, charset));
    }

    public void read(Reader reader) throws IOException {
        List<String> lines = readLines(reader);
        for (String line : lines) {
            if (line.startsWith("#")) {
                continue;
            }
            if (line == null || line.isEmpty()) {
                continue;
            }
            int index = line.indexOf("=");
            String key = line.substring(0, index);
            String value = line.substring(index + 1);
            key = key.trim();
            value = value.trim();
            if (value.equalsIgnoreCase("NULL")) {
                this.remove(key);
            } else {
                this.put(key, value);
            }
        }
    }

    public void store(OutputStream out, String charset) throws IOException {
        store(new OutputStreamWriter(out, charset));
    }

    public void store(Writer writer) throws IOException {
        List<String> keys = new ArrayList<String>();
        keys.addAll(this.keySet());
        Collections.sort(keys);

        for (String key : keys) {
            writer.write(key);
            writer.write("=");
            writer.write(this.get(key));
            writer.write("\r\n");
        }
        writer.flush();
        writer.close();
    }

    public void append(File file) throws IOException {
        store(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
    }

    private List<String> readLines(Reader reader) throws IOException {
        List<String> list = new ArrayList<>();
        BufferedReader br = new BufferedReader(reader);
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            list.add(line);
        }
        return list;
    }
}
