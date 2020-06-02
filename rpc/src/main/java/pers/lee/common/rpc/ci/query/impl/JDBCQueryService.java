package pers.lee.common.rpc.ci.query.impl;

import pers.lee.common.rpc.ci.query.Constraint;
import pers.lee.common.rpc.ci.query.GroupDescriptor;
import pers.lee.common.rpc.ci.query.QueryService;
import pers.lee.common.rpc.ci.query.ResultDescriptor;

import java.util.HashMap;
import java.util.List;

/**
 * Not Implemented
 * 
 * @author yangyang
 * @since 2009-10-23
 */
public class JDBCQueryService implements QueryService {

	public HashMap<String, Object> getEntities() {
		throw new UnsupportedOperationException();
	}

	public List<?> query(String entityName, Constraint constraint, ResultDescriptor resultDescriptor) {
		throw new UnsupportedOperationException();
	}

	public int delete(String entityName, Constraint constraint) {
		throw new UnsupportedOperationException();
	}

	public List<?> group(String entityName, Constraint constraint, GroupDescriptor groupDescriptor) {
		throw new UnsupportedOperationException();
	}
}
