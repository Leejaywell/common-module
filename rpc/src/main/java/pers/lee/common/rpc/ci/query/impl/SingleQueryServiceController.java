package pers.lee.common.rpc.ci.query.impl;

import pers.lee.common.rpc.ci.query.*;
import pers.lee.common.lang.json.TypeIntrospector;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author yangyang
 * @since 2009-10-23
 */
public class SingleQueryServiceController implements QueryService {

    private HashMap<String, SingleQueryService> serviceMap = new HashMap<>();

    public void setSingleQueryServices(Set<SingleQueryService> singleQueryServices) {
        for (SingleQueryService singleQueryService : singleQueryServices) {
            addSingleQueryService(singleQueryService);
        }
    }

    public void addSingleQueryService(SingleQueryService singleQueryService) {
        serviceMap.put(singleQueryService.getEntityName(), singleQueryService);
    }

    public HashMap<String, Object> getEntities() {
        HashMap<String, Object> entities = new HashMap<String, Object>();
        for (SingleQueryService service : serviceMap.values()) {
            entities.put(service.getEntityName(), new TypeIntrospector().introspect((Type) service.getEntityType()));
        }
        return entities;
    }

    public List<?> query(String entityName, Constraint constraint, ResultDescriptor resultDescriptor) {
        return serviceMap.get(entityName).query(constraint, resultDescriptor);
    }

    public int delete(String entityName, Constraint constraint) {
        return serviceMap.get(entityName).delete(constraint);
    }

    public List<?> group(String entityName, Constraint constraint, GroupDescriptor groupDescriptor) {
        return serviceMap.get(entityName).group(constraint, groupDescriptor);
    }
}
