package pers.lee.common.rpc.ci.detect;

/**
 * WSResponseVerifier
 *
 * @author Drizzt Yang
 */
public interface WSResponseVerifier {
    
    boolean verify(String response);
}
