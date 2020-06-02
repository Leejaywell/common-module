package pers.lee.common.rpc.ci.detect;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * DetectorManager
 *
 * @author Drizzt Yang
 */
public class DetectorManager implements DetectService {
    private List<Detector> detectors = new ArrayList<Detector>();

    @Override
    public List<DetectResult> detect() {
        List<DetectResult> detectResults = new ArrayList<DetectResult>();
        for (Detector detector : detectors) {
            detectResults.add(detector.detect());
        }
        return detectResults;
    }

    @Override
    public List<DetectResult> detectOnly(Set<String> detectorNames) {
        List<DetectResult> detectResults = new ArrayList<DetectResult>();
        for (Detector detector : detectors) {
            if(detectorNames.contains(detector.getDetectorName())) {
                detectResults.add(detector.detect());
            } else {
                detectResults.add(DetectResult.skip(detector.getDetectorName()));
            }
        }
        return detectResults;
    }

    public void register(Detector detector) {
        detectors.add(detector);
    }

    static DetectorManager detectorManager = new DetectorManager();

    public static DetectorManager get() {
        return detectorManager;
    }
}
