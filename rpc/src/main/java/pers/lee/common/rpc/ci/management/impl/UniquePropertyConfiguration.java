package pers.lee.common.rpc.ci.management.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author YangYang
 * @version 0.1, 2008-10-18 12:49:50
 */
public class UniquePropertyConfiguration {
    private Map<String, Set<Set<String>>> entityUniquePropertyNameSets;

    public boolean existUniqueProperty(String name) {
        return entityUniquePropertyNameSets.get(name) != null;
    }

    public Set<Set<String>> getUniquePropertySet(String name) {
        return entityUniquePropertyNameSets.get(name);
    }

    public void setEntityUniquePropertyNamesSet(Map<String, Set<String>> entityUniquePropertyNamesSet) {
        this.entityUniquePropertyNameSets = new HashMap<String, Set<Set<String>>>();
        for (String key : entityUniquePropertyNamesSet.keySet()) {
            Set<Set<String>> uniquePropertyNameSets = new HashSet<Set<String>>();
            for (String uniquePropertyNames : entityUniquePropertyNamesSet.get(key)) {
                Set<String> uniquePropertyNameSet = new HashSet<String>();
                uniquePropertyNameSets.add(uniquePropertyNameSet);
                for (String uniquePropertyName : uniquePropertyNames.split(",")) {
                    if (!uniquePropertyName.trim().equals("")) {
                        uniquePropertyNameSet.add(uniquePropertyName.trim());
                    }
                }
            }
            this.entityUniquePropertyNameSets.put(key, uniquePropertyNameSets);
        }
    }

}
