package pers.lee.common.rpc.ci.detect;

/**
 * DetectorBase
 *
 * @author Drizzt Yang
 */
public abstract class DetectorBase implements Detector {
    protected String detectorName;

    public String getDetectorName() {
        return detectorName;
    }

    public void setDetectorName(String detectorName) {
        this.detectorName = detectorName;
    }

    protected DetectResult pass() {
        return DetectResult.pass(detectorName, null);
    }

    protected DetectResult fail() {
        return DetectResult.fail(detectorName, null);
    }

    protected DetectResult pass(String message) {
        return DetectResult.pass(detectorName, message);
    }

    protected DetectResult fail(String message) {
        return DetectResult.fail(detectorName, message);
    }

    protected DetectResult skip() {
        return DetectResult.skip(detectorName);
    }
}
