package pers.lee.common.config.component;

import pers.lee.common.config.Configuration;
import pers.lee.common.lang.properties.SortStringMap;

import java.io.*;
import java.util.Map;

/**
 * FileConfiguration
 *
 * @author Drizzt Yang
 */
public class FileConfiguration extends ReloadableConfiguration implements Configuration {
    private File file;

    public FileConfiguration(String name, File file) {
        this.name = name;
        this.file = file;
    }

    public FileConfiguration(String name, File file, boolean reloadActivated) {
        super(name, reloadActivated);
        this.file = file;
    }

    @Override
    public void setProperty(String key, String value) {
        SortStringMap sortStringMap = this.toStringMap();
        sortStringMap.put(key, value);
        save(sortStringMap);
        super.setProperty(key, value);
    }

    @Override
    public void clearProperty(String key) {
        SortStringMap sortStringMap = this.toStringMap();
        sortStringMap.remove(key);
        save(sortStringMap);
        super.clearProperty(key);
    }

    protected void save(SortStringMap sortStringMap) {
        try {
            sortStringMap.store(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("save Configuration to file [" + file.getPath() + "] failed", e);
        }
        timestamp = getCurrentTimestamp();
    }

    @Override
    protected long getCurrentTimestamp() {
        return file.lastModified();
    }

    @Override
    protected Map<String, String> reload() {
        if (!file.exists()) {
            throw new IllegalArgumentException("config file [" + file.getPath() + "] is not found");
        }
        SortStringMap stringMap = new SortStringMap();
        try {
            stringMap.read(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("load Configuration from file [" + file.getPath() + "] failed", e);
        }
        this.timestamp = file.lastModified();
        return stringMap;
    }

    public File getFile() {
        return file;
    }

    @Override
    public boolean available() {
        return file.exists();
    }
}
