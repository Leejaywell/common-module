package pers.lee.common.rpc.ci.management;

import java.util.Map;

/**
 * @author YangYang
 * @version 0.1, 2008-10-22 19:07:14
 */
@SuppressWarnings("unchecked")
public interface SingleEntityService {
	String getEntityName();

	Object getEntityType();

	Object get(Map properties);

	Object put(Map properties);

	void delete(Map properties);
}
