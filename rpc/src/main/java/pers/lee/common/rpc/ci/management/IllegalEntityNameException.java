package pers.lee.common.rpc.ci.management;

/**
 * Throws when the entity name is illegal to be processed
 * @author YangYang
 * @version 0.1, 2008-11-28 14:37:36
 */
public class IllegalEntityNameException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public IllegalEntityNameException(String name) {
        super("Illegal entity name [" + name + "]");
    }
}
