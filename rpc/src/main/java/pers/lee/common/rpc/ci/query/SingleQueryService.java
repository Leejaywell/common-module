package pers.lee.common.rpc.ci.query;

import java.util.List;

/**
 * @author yangyang
 * @since 2009-10-23
 */
public interface SingleQueryService {

    String getEntityName();

    Object getEntityType();

    /**
     * Do the query
     * @param constraint the constraint
     * @param resultDescriptor the result descriptor
     * @return the query result
     */
    List<?> query(Constraint constraint, ResultDescriptor resultDescriptor);

    /**
     * Delete the entities fulfilled the constraint
     * @param constraint the constraint
     * @return the delete entity count
     */
    int delete(Constraint constraint);

    /**
     * Group by some property to do some function
     * @param constraint the constraint
     * @param groupDescriptor the group descriptor
     * @return the group result
     */
    List<?> group(Constraint constraint, GroupDescriptor groupDescriptor);


}
