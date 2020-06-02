package pers.lee.common.rpc.ci.query.impl;

import pers.lee.common.rpc.ci.query.*;

import java.util.HashMap;
import java.util.List;

/**
 * @author yangyang
 * @since 2009-10-23
 */
public class ComposedQueryService implements QueryService {
	
    private List<QueryService> queryServices;

    public HashMap<String, Object> getEntities() {
        HashMap<String, Object> entities = new HashMap<String, Object>();
		for (QueryService queryService : queryServices) {
			entities.putAll(queryService.getEntities());
		}
		return entities;
    }

    private QueryService getAvailableService(String name) {
		QueryService availableService = null;
		for (QueryService queryService : queryServices) {
			if (queryService.getEntities().keySet().contains(name)) {
				availableService = queryService;
			}
		}
		if (availableService == null) {
			throw new IllegalEntityNameException(name);
		}
		return availableService;
	}

    public List<?> query(String entityName, Constraint constraint, ResultDescriptor resultDescriptor) {
        return getAvailableService(entityName).query(entityName, constraint, resultDescriptor);
    }

    public int delete(String entityName, Constraint constraint) {
        return getAvailableService(entityName).delete(entityName, constraint);
    }

    public List<?> group(String entityName, Constraint constraint, GroupDescriptor groupDescriptor) {
        return getAvailableService(entityName).group(entityName, constraint, groupDescriptor);
    }

    public List<QueryService> getQueryServices() {
        return queryServices;
    }

    public void setQueryServices(List<QueryService> queryServices) {
        this.queryServices = queryServices;
    }
}
