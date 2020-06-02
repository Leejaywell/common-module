package pers.lee.common.config.component;

import pers.lee.common.config.Configuration;
import pers.lee.common.config.ConfigurationListener;
import pers.lee.common.lang.json.Json;
import pers.lee.common.lang.properties.SortStringMap;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * BaseConfiguration
 *
 * @author Drizzt Yang
 */
public class BaseConfiguration implements Configuration {

    protected String name;
    protected Map<String, String> stringMap = new SortStringMap();
    protected List<ConfigurationListener> configurationListeners = new ArrayList<ConfigurationListener>();

    public BaseConfiguration() {
        name = "default";
    }

    public BaseConfiguration(String name) {
        this.name = name;
    }

    public BaseConfiguration(String name, SortStringMap sortStringMap) {
        this.name = name;
        this.stringMap = sortStringMap;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void init() {
        fireInit();
    }

    protected void fireInit() {
        for (ConfigurationListener listener : configurationListeners) {
            listener.notifyInit(this);
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void addListener(ConfigurationListener listener) {
        configurationListeners.add(listener);
    }

    @Override
    public Configuration subset(String prefix) {
        BaseConfiguration baseConfiguration = new BaseConfiguration();
        for (String key : this.getKeys(prefix)) {
            baseConfiguration.setProperty(key, this.getProperty(key));
        }
        return baseConfiguration;
    }

    @Override
    public boolean isEmpty() {
        return stringMap.isEmpty();
    }

    @Override
    public boolean containsKey(String key) {
        return stringMap.containsKey(key);
    }

    @Override
    public void clearProperty(String key) {
        if (stringMap.containsKey(key)) {
            stringMap.remove(key);
            fireChanges(key);
        }
    }

    protected void fireChanges(String key) {
        for (ConfigurationListener listener : configurationListeners) {
            listener.notifyUpdate(this, key);
        }
    }

    @Override
    public void setProperty(String key, String value) {
        if (!value.equals(stringMap.get(key))) {
            stringMap.put(key, value);
            fireChanges(key);
        }
    }

    @Override
    public void setProperty(String key, Boolean value) {
        this.setProperty(key, value.toString());
    }

    @Override
    public void setProperty(String key, Integer value) {
        this.setProperty(key, value.toString());
    }

    @Override
    public void setProperty(String key, Long value) {
        this.setProperty(key, value.toString());
    }

    @Override
    public void setProperty(String key, BigDecimal value) {
        this.setProperty(key, value.toString());
    }

    @Override
    public void setProperty(String key, BigInteger value) {
        this.setProperty(key, value.toString());
    }

    @Override
    public void setProperty(String key, List<String> value) {
        setJSONProperty(key, value);
    }

    @Override
    public void setProperty(String key, Set<String> value) {
        setJSONProperty(key, value);
    }

    @Override
    public void setProperty(String key, Map<String, String> value) {
        setJSONProperty(key, value);
    }

    @Override
    public void setProperty(String key, Properties value) {
        setJSONProperty(key, value);
    }

    protected void setJSONProperty(String key, Object value) {
        this.setProperty(key, Json.getDefault().toJsonString(value));
    }

    @Override
    public String getProperty(String key) {
        return stringMap.get(key);
    }

    @Override
    public Set<String> getKeys(final String prefix) {
        Set<String> keys = new HashSet<String>();
        for (Map.Entry<String, String> entry : stringMap.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(prefix)) {
                keys.add(key);
            }
        }
        return keys;
    }

    @Override
    public Set<String> getKeys() {
        return stringMap.keySet();
    }

    @Override
    public Boolean getBoolean(String key) {
        String property = getProperty(key);
        return property != null ? Boolean.parseBoolean(property) : null;
    }

    @Override
    public Boolean getBoolean(String key, Boolean defaultValue) {
        String property = getProperty(key);
        return property != null ? Boolean.parseBoolean(property) : defaultValue;
    }

    @Override
    public Integer getInteger(String key) {
        String property = getProperty(key);
        return property != null ? Integer.parseInt(property) : null;
    }

    @Override
    public Integer getInteger(String key, Integer defaultValue) {
        String property = getProperty(key);
        return property != null ? Integer.parseInt(property) : defaultValue;
    }

    @Override
    public Long getLong(String key) {
        String property = getProperty(key);
        return property != null ? Long.parseLong(property) : null;
    }

    @Override
    public Long getLong(String key, Long defaultValue) {
        String property = getProperty(key);
        return property != null ? Long.parseLong(property) : defaultValue;
    }

    @Override
    public BigDecimal getBigDecimal(String key) {
        String property = getProperty(key);
        return property != null ? new BigDecimal(property) : null;
    }

    @Override
    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        String property = getProperty(key);
        return property != null ? new BigDecimal(property) : defaultValue;
    }

    @Override
    public BigInteger getBigInteger(String key) {
        String property = getProperty(key);
        return property != null ? new BigInteger(property) : null;
    }

    @Override
    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        String property = getProperty(key);
        return property != null ? new BigInteger(property) : defaultValue;
    }

    @Override
    public String getString(String key) {
        return getProperty(key);
    }

    @Override
    public String getString(String key, String defaultValue) {
        String property = getProperty(key);
        return property != null ? property : defaultValue;
    }

    protected Object getJSONProperty(String key, Type type) {
        if (this.getProperty(key) == null) {
            return null;
        }

        try {
            return Json.getDefault().deserialize(new StringReader(this.getProperty(key)), type);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<String> getList(String key) {
        String valueString = getString(key);
        if (valueString == null) {
            return null;
        }
        if (valueString.startsWith("[")) {
            return (List<String>) getJSONProperty(key, ArrayList.class);
        }
        List<String> values = new ArrayList<String>();
        values.addAll(Arrays.asList(valueString.split(",")));
        return values;
    }

    @Override
    public <T> List<T> getList(String key, Class<T> clazz) {
        return (List<T>) getJSONProperty(key, SimpleParameterizedType.createCollectionType(ArrayList.class, clazz));
    }

    @Override
    public Set<String> getSet(String key) {
        String valueString = getString(key);
        if (valueString == null) {
            return null;
        }
        if (valueString.startsWith("[")) {
            return (Set<String>) getJSONProperty(key, HashSet.class);
        }
        Set<String> values = new HashSet<String>();
        values.addAll(Arrays.asList(valueString.split(",")));
        return values;
    }

    @Override
    public <T> Set<T> getSet(String key, Class<T> type) {
        return (Set<T>) getJSONProperty(key, SimpleParameterizedType.createCollectionType(HashSet.class, type));
    }

    @Override
    public Properties getProperties(String key) {
        String valueString = getString(key);
        if (valueString == null) {
            return null;
        }
        if (valueString.startsWith("{")) {
            return (Properties) getJSONProperty(key, Properties.class);
        }
        Properties values = new Properties();
        for (String valuePair : Arrays.asList(valueString.split(","))) {
            List<String> valuePairParts = Arrays.asList(valuePair.split(":"));
            if (valuePairParts.size() > 1) {
                values.setProperty(valuePairParts.get(0), valuePairParts.get(1));
            }
        }
        return values;
    }

    @Override
    public Map<String, String> getMap(String key) {
        String valueString = getString(key);
        if (valueString == null) {
            return null;
        }
        if (valueString.startsWith("{")) {
            return (Map<String, String>) getJSONProperty(key, SimpleParameterizedType.createMapType(HashMap.class, String.class, String.class));

        }
        Map<String, String> values = new HashMap<String, String>();
        for (String valuePair : Arrays.asList(valueString.split(","))) {
            List<String> valuePairParts = Arrays.asList(valuePair.split(":"));
            if (valuePairParts.size() > 1) {
                values.put(valuePairParts.get(0), valuePairParts.get(1));
            }
        }
        return values;
    }

    @Override
    public <T> Map<String, T> getMap(String key, Class<T> type) {
        return (Map<String, T>) getJSONProperty(key, SimpleParameterizedType.createMapType(HashMap.class, String.class, type));
    }

    @Override
    public <T> T getObject(String key, Class<T> clazz) {
        return (T) getJSONProperty(key, clazz);
    }

    @Override
    public Properties toProperties() {
        Properties properties = new Properties();
        for (Map.Entry<String, String> entry : stringMap.entrySet()) {
            properties.setProperty(entry.getKey(), entry.getValue());
        }
        return properties;
    }

    @Override
    public SortStringMap toStringMap() {
        SortStringMap newSortStringMap = new SortStringMap();
        for (Map.Entry<String, String> entry : stringMap.entrySet()) {
            newSortStringMap.put(entry.getKey(), entry.getValue());
        }
        return newSortStringMap;
    }

    @Override
    public boolean available() {
        return true;
    }

    private static class SimpleParameterizedType implements ParameterizedType {
        private Class ownerType;
        private Type[] argumentTypes;

        private static SimpleParameterizedType createCollectionType(Class collectionClass, Class itemType) {
            SimpleParameterizedType simpleParameterizedType = new SimpleParameterizedType();
            simpleParameterizedType.ownerType = collectionClass;
            simpleParameterizedType.argumentTypes = new Type[]{itemType};

            return simpleParameterizedType;
        }

        private static SimpleParameterizedType createMapType(Class mapClass, Class keyType, Class valueType) {
            SimpleParameterizedType simpleParameterizedType = new SimpleParameterizedType();
            simpleParameterizedType.ownerType = mapClass;
            simpleParameterizedType.argumentTypes = new Type[]{keyType, valueType};

            return simpleParameterizedType;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return argumentTypes;
        }

        @Override
        public Type getRawType() {
            return ownerType;
        }

        @Override
        public Type getOwnerType() {
            return ownerType;
        }
    }
}
