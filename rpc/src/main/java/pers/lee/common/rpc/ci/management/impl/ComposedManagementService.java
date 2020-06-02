package pers.lee.common.rpc.ci.management.impl;

import pers.lee.common.rpc.ci.management.IllegalEntityNameException;
import pers.lee.common.rpc.ci.management.ManagementService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Compose a list of management services. The latter service in the list will
 * replace the one former
 * 
 * @author YangYang
 * @version 0.1, 2008-11-4 0:13:45
 */
@SuppressWarnings("unchecked")
public class ComposedManagementService extends BaseManagementService implements ManagementService {
	private Map<String, ManagementService> managementServices = new HashMap<String, ManagementService>();

	public HashMap<String, Object> getEntities() {
		HashMap<String, Object> entities = new HashMap<String, Object>();
		for (ManagementService managementService : managementServices.values()) {
			entities.putAll(managementService.getEntities());
		}
		return entities;
	}

	public Object get(String name, Map properties) {
		return getAvailableManagementService(name).get(name, properties);
	}

	private ManagementService getAvailableManagementService(String name) {
		ManagementService availableManagementService = managementServices.get(name);
		if (availableManagementService == null) {
			throw new IllegalEntityNameException(name);
		}
		return availableManagementService;
	}

	public Object put(String name, Map properties) {
		return getAvailableManagementService(name).put(name, properties);
	}

	public void delete(String name, Map properties) {
		getAvailableManagementService(name).delete(name, properties);
	}

	public void setManagementServices(List<ManagementService> managementServices) {
        for (ManagementService managementService : managementServices) {
            for (Map.Entry<String, Object> entry : managementService.getEntities().entrySet()) {
                this.managementServices.put(entry.getKey(), managementService);
            }
        }
	}
}
