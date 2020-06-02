package pers.lee.common.rpc.ci.spring;

import java.util.HashMap;
import java.util.Map;

/**
 * BeanStatusWatchConfiguration
 *
 * @author Drizzt Yang
 */
public class BeanStatusWatchConfiguration {
    private Map<String, String> beanStatusKeyMap = new HashMap<String, String>();
    private Map<String, Map<String, String>> propertyKeysMap = new HashMap<String, Map<String, String>>();

    public String getBeanStatusKey(String beanName) {
        return beanStatusKeyMap.get(beanName);
    }

    public Map<String, String> getPropertyStatusKeys(String beanName) {
        return propertyKeysMap.get(beanName);
    }

    public void setBeanStatusKeyMap(Map<String, String> beanStatusKeyMap) {
        this.beanStatusKeyMap = beanStatusKeyMap;
    }

}
