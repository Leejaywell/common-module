package pers.lee.common.rpc.ci.detect;

/**
 * WSContainsVerifier
 *
 * @author Drizzt Yang
 */
public class WSContainsVerifier implements WSResponseVerifier {
    private String successTag;

    public WSContainsVerifier(String successTag) {
        this.successTag = successTag;
    }

    @Override
    public boolean verify(String response) {
        return response.contains(successTag);
    }

    @Override
    public String toString() {
        return "Contains Verifier [" + successTag + ']';
    }

}
