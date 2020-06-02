package pers.lee.common.rpc.client;

/**
 * JsonRpcException
 *
 * @author Drizzt Yang
 */
public class JsonRpcException extends RuntimeException {

    private final String type;
    private final String detail;

    public JsonRpcException(String type, String detail) {
        super(type + ": " + detail);
        this.type = type;
        this.detail = detail;
    }

    public String getType() {
        return type;
    }

    public String getDetail() {
        return detail;
    }
}
