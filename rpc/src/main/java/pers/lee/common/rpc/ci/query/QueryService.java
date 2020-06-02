package pers.lee.common.rpc.ci.query;

import java.util.HashMap;
import java.util.List;

/**
 * The core query service interface
 * @author YangYang
 * @version 0.1, 2008-5-27 21:22:21
 */
public interface QueryService {
    /**
     * Get the available entities in QueryService service.
     * @return a list of the available entities
     */
    HashMap<String, Object> getEntities();

    /**
     * Do the query
     * @param entityName entity name
     * @param constraint the constraint
     * @param resultDescriptor the result descriptor
     * @return the query result list
     */
    List<?> query(String entityName, Constraint constraint, ResultDescriptor resultDescriptor);

    /**
     * Delete the entities fulfilled the constraint
     * @param entityName entity name
     * @param constraint the constraint
     * @return the delete entity count
     */
    int delete(String entityName, Constraint constraint);

    /**
     * Group by some property to do some function
     * @param entityName entity name
     * @param constraint the constraint
     * @param groupDescriptor the group descriptor
     * @return the group result
     */
    List<?> group(String entityName, Constraint constraint, GroupDescriptor groupDescriptor);

}
