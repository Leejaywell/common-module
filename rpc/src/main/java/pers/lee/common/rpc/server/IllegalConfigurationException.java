package pers.lee.common.rpc.server;

/**
 * @author YangYang
 * @version 0.1, 2008-3-17 17:00:46
 */
public class IllegalConfigurationException extends Exception {

	private static final long serialVersionUID = -1573694537685882639L;

	public IllegalConfigurationException(String message) {
		super(message);
	}

	public IllegalConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}
