package pers.lee.common.rpc.ci.management.impl;

import pers.lee.common.lang.json.TypeIntrospector;
import pers.lee.common.rpc.ci.management.SingleEntityService;

import java.lang.reflect.ParameterizedType;

/**
 * @author YangYang
 * @version 0.1, 2008-11-3 23:58:20
 */
@SuppressWarnings("unchecked")
public abstract class CustomisedEntityService<T> implements SingleEntityService {

    public Class getEntityClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public String getEntityName() {
        return getEntityClass().getName();
    }

    public Object getEntityType() {
        return new TypeIntrospector().introspect(getEntityClass());
    }
}
