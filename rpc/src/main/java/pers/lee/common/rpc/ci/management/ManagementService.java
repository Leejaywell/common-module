package pers.lee.common.rpc.ci.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author YangYang
 * @version 0.1, 2008-9-20 21:05:37
 */
@SuppressWarnings("unchecked")
public interface ManagementService {
	/**
	 * Get the available entities in Query service.
	 * 
	 * @return a list of the available entities
	 */
	HashMap<String, Object> getEntities();

	Object get(String name, Map properties);

	Object put(String name, Map properties);

	void delete(String name, Map properties);

    List<Object> batchGet(String name, List<Map> batchProperties);

	List<Object> batchPut(String name, List<Map> entities);

	void batchDelete(String name, List<Map> entities);
}
