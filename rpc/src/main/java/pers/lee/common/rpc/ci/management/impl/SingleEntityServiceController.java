package pers.lee.common.rpc.ci.management.impl;

import pers.lee.common.rpc.ci.management.ManagementService;
import pers.lee.common.rpc.ci.management.SingleEntityService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author YangYang
 * @version 0.1, 2008-10-22 19:08:43
 */
@SuppressWarnings("unchecked")
public class SingleEntityServiceController extends BaseManagementService implements ManagementService {
	private HashMap<String, SingleEntityService> entityMap = new HashMap<String, SingleEntityService>();

	public void setSingleEntityServices(Set<SingleEntityService> singleEntityServices) {
		for (SingleEntityService singleEntityService : singleEntityServices) {
			entityMap.put(singleEntityService.getEntityName(), singleEntityService);
		}
	}

	public void setSingleEntityServiceConfig(SingleEntityServiceConfiguration singleEntityServiceConfiguration) {
		if (singleEntityServiceConfiguration == null || singleEntityServiceConfiguration.getSingleEntityServices() == null) {
			return;
		}
		this.setSingleEntityServices(singleEntityServiceConfiguration.getSingleEntityServices());
	}

	public HashMap<String, Object> getEntities() {
		HashMap<String, Object> entities = new HashMap<String, Object>();
		for (SingleEntityService singleEntityService : entityMap.values()) {
			entities.put(singleEntityService.getEntityName(), singleEntityService.getEntityType());
		}
		return entities;
	}

	public Object get(String name,  Map properties) {
		return entityMap.get(name).get(properties);
	}

	public Object put(String name, Map properties) {
		return entityMap.get(name).put(properties);
	}

	public void delete(String name, Map properties) {
		entityMap.get(name).delete(properties);
	}

}
