package pers.lee.common.rpc.ci.detect;

/**
 * Detector
 *
 * @author Drizzt Yang
 */
public interface Detector {
    String getDetectorName();

    DetectResult detect();
}
