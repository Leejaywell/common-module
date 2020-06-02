package pers.lee.common.rpc.server;

/**
 * The exception occured in rpc invocation
 * 
 * @author YangYang
 * @version 0.1, 2008-9-22 13:32:43
 */
public class RpcException extends RuntimeException {

	private static final long serialVersionUID = 5056853591363234267L;

    private String errorCode = "RPCExecuteError";

    public RpcException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public RpcException(String message) {
		super(message);
	}

	public RpcException(String message, Throwable cause) {
		super(message, cause);
	}

    public String getErrorCode() {
        return errorCode;
    }
}
