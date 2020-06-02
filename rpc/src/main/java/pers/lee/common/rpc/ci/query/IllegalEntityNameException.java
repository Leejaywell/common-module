package pers.lee.common.rpc.ci.query;

/**
 * @author yangyang
 * @since 2009-10-23
 */
public class IllegalEntityNameException extends RuntimeException {

	private static final long serialVersionUID = -2064591956053938645L;

	public IllegalEntityNameException(String name) {
        super("Illegal entity name [" + name + "]");
    }
}