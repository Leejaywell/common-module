package pers.lee.common.rpc.ci.management.impl;

import pers.lee.common.rpc.ci.management.SingleEntityService;

import java.util.HashMap;

/**
 * @author YangYang
 * @version 0.1, 2008-11-3 23:53:12
 */
@SuppressWarnings("unchecked")
public abstract class VirtualEntityService implements SingleEntityService {
	private String entityName;

	private HashMap entity;

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public Object getEntityType() {
		return entity;
	}

	public void setEntity(HashMap entity) {
		this.entity = entity;
	}
}
