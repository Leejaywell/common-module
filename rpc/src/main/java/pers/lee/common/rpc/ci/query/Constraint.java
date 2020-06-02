package pers.lee.common.rpc.ci.query;

/**
 * @author YangYang
 * @version 0.1, 2008-5-27 21:28:20
 */
public interface Constraint {
	
    boolean isNot();

    void setNot(boolean not);
}
