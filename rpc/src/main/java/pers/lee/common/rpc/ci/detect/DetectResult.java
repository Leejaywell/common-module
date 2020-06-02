package pers.lee.common.rpc.ci.detect;

/**
 * DetectResult
 *
 * @author Drizzt Yang
 */
public class DetectResult {
    public static final String RESULT_CODE_PASS = "Pass";
    public static final String RESULT_CODE_FAIL = "Fail";
    public static final String RESULT_CODE_SKIP = "Skip";

    private String detectorName;
    private String resultCode;
    private String resultMessage;

    public String getDetectorName() {
        return detectorName;
    }

    public void setDetectorName(String detectorName) {
        this.detectorName = detectorName;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public boolean isPass() {
        return RESULT_CODE_PASS.equals(resultCode);
    }

    public boolean isFail() {
        return RESULT_CODE_FAIL.equals(resultCode);
    }
    
    public static DetectResult skip(String detectorName) {
        DetectResult detectResult = new DetectResult();
        detectResult.setDetectorName(detectorName);
        detectResult.setResultCode(RESULT_CODE_SKIP);
        return detectResult;
    }

    public static DetectResult pass(String detectorName, String message) {
        DetectResult detectResult = new DetectResult();
        detectResult.setDetectorName(detectorName);
        detectResult.setResultCode(RESULT_CODE_PASS);
        detectResult.setResultMessage(message);
        return detectResult;
    }

    public static DetectResult fail(String detectorName, String message) {
        DetectResult detectResult = new DetectResult();
        detectResult.setDetectorName(detectorName);
        detectResult.setResultCode(RESULT_CODE_FAIL);
        detectResult.setResultMessage(message);
        return detectResult;
    }
}
