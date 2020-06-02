package pers.lee.common.rpc.ci.query.impl;

import pers.lee.common.rpc.ci.query.Constraint;
import pers.lee.common.rpc.ci.query.GroupDescriptor;
import pers.lee.common.rpc.ci.query.QueryService;
import pers.lee.common.rpc.ci.query.ResultDescriptor;

import java.util.HashMap;
import java.util.List;

/**
 * @author YangYang
 * @version 0.1, 2008-10-22 13:47:02
 */
public class QueryServiceWrapper implements QueryService {

    private QueryService queryService;

    private HashMap<String, String> alias2Name;

    private boolean init = false;

    public HashMap<String, Object> getEntities() {
        if(!init) {
            initAlias();
        }
        HashMap<String, Object> entities = new HashMap<String, Object>();
        for(String alias : alias2Name.keySet()) {
            String name = alias2Name.get(alias);
            entities.put(alias, queryService.getEntities().get(name));
        }
        return entities;
    }
    
    private synchronized void initAlias() {
        if(init) {
            return;
        }
        alias2Name = new HashMap<String, String>();
        for(String key : queryService.getEntities().keySet()) {
            String alias = key.substring(key.lastIndexOf(".") + 1);
            if (alias2Name.get(alias) != null) {
                alias = key;
            }
            alias2Name.put(alias, key);
        }
        init = true;
    }

    private String toName(String alias) {
        if(!init) {
            initAlias();
        }
        String name = alias2Name.get(alias);
        name = name == null ? alias : name;
        return name;
    }

    public List<?> query(String alias, Constraint constraint, ResultDescriptor resultDescriptor) {
        return queryService.query(toName(alias), constraint, resultDescriptor);
    }

    public int delete(String alias, Constraint constraint) {
        return queryService.delete(toName(alias), constraint);
    }

    public List<?> group(String alias, Constraint constraint, GroupDescriptor groupDescriptor) {
        return queryService.group(toName(alias), constraint, groupDescriptor);
    }

    public QueryService getQueryService() {
        return queryService;
    }

    public void setQueryService(QueryService queryService) {
        this.queryService = queryService;
    }
}
