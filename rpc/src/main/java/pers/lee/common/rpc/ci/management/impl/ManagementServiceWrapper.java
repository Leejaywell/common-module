package pers.lee.common.rpc.ci.management.impl;

import pers.lee.common.lang.json.Json;
import pers.lee.common.rpc.ci.management.ManagementService;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YangYang
 * @version 0.1, 2008-10-20 16:59:22
 */
@SuppressWarnings("unchecked")
public class ManagementServiceWrapper extends BaseManagementService implements ManagementService {
    private ManagementService managementService;

    private HashMap<String, String> alias2Name;

    private boolean init = false;

    public HashMap<String, Object> getEntities() {
        if (!init) {
            initAlias();
        }
        HashMap<String, Object> entities = new HashMap<String, Object>();
        for (String alias : alias2Name.keySet()) {
            String name = alias2Name.get(alias);
            entities.put(alias, managementService.getEntities().get(name));
        }
        return entities;
    }

    public Object get(String alias, Map properties) {
        return managementService.get(toName(alias), properties);
    }

    private synchronized void initAlias() {
        if (init) {
            return;
        }
        alias2Name = new HashMap<String, String>();
        for (String key : managementService.getEntities().keySet()) {
            String alias = key.substring(key.lastIndexOf(".") + 1);
            if (alias2Name.get(alias) != null) {
                alias = key;
            }
            alias2Name.put(alias, key);
        }
        init = true;
    }

    public Object put(String alias, Map properties) {
        String name = toName(alias);
        Object result = managementService.put(name, properties);
        LoggerFactory.getLogger("rpc.put").info(name + " | " + "put | " + toJSONString(properties));
        return result;
    }

    private String toJSONString(Map properties) {
        StringWriter writer = new StringWriter();
        try {
            Json.getDefault().serialize(writer, properties);
        } catch (IOException e) {
            throw new RuntimeException("json serialize failed", e);
        }
        return writer.toString();
    }

    private String toName(String alias) {
        if (!init) {
            initAlias();
        }
        String name = alias2Name.get(alias);
        name = name == null ? alias : name;
        return name;
    }

    public void delete(String alias, Map properties) {
        String name = toName(alias);
        managementService.delete(name, properties);
        LoggerFactory.getLogger("rpc.delete").info(name + " | " + "delete | " + toJSONString(properties));
    }

    public ManagementService getManagementService() {
        return managementService;
    }

    public void setManagementService(ManagementService managementService) {
        this.managementService = managementService;
    }
}
