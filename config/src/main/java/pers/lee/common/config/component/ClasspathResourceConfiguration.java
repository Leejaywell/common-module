package pers.lee.common.config.component;

import pers.lee.common.lang.properties.SortStringMap;

import java.io.IOException;
import java.io.InputStream;

/**
 * ClasspathResourceConfiguration
 *
 * @author Drizzt Yang
 */
public class ClasspathResourceConfiguration extends BaseConfiguration {
    private String classpath;

    public ClasspathResourceConfiguration(String classpath) {
        super("classpath: " + classpath);
        this.classpath = classpath;
    }

    @Override
    public void init() {
        try {
            try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(classpath)) {
                SortStringMap sortStringMap = new SortStringMap();
                sortStringMap.read(inputStream, "UTF-8");
                this.stringMap = sortStringMap;
            }
        } catch (IOException e) {
            throw new RuntimeException("load properties failed", e);
        }
        super.init();
    }

    @Override
    public boolean available() {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(classpath);
        return inputStream != null;
    }

    public String getClasspath() {
        return classpath;
    }
}
