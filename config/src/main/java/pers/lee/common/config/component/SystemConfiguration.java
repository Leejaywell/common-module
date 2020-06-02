package pers.lee.common.config.component;

import pers.lee.common.config.Configuration;
import pers.lee.common.lang.properties.SortStringMap;
import pers.lee.common.lang.utils.IPUtils;

import java.util.Map;

/**
 * SystemConfiguration
 *
 * @author Drizzt Yang
 */
public class SystemConfiguration extends BaseConfiguration implements Configuration {

    public static final String NAME = "system";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void init() {
        stringMap = new SortStringMap();
        for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
            stringMap.put(entry.getKey().toString(), entry.getValue().toString());
        }

        stringMap.putAll(IPUtils.ipInfo());

        super.init();
    }
}
