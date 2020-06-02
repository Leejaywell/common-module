package pers.lee.common.rpc.ci.spring;

import pers.lee.common.rpc.ci.detect.DetectResult;
import pers.lee.common.rpc.ci.detect.Detector;
import pers.lee.common.rpc.ci.detect.DetectorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * SpringDetectorInitializing
 *
 * @author Drizzt Yang
 */
public class SpringDetectorInitializing implements InitializingBean {
    public static final Logger logger = LoggerFactory.getLogger(SpringDetectorInitializing.class);

    private DetectorManager detectorManager;
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String,Detector> detectorMap = applicationContext.getBeansOfType(Detector.class);
        for (Detector detector : detectorMap.values()) {
            detectorManager.register(detector);
            DetectResult detectResult = detector.detect();
            if(detectResult.isFail()) {
                logger.error("Detect " + detector.getDetectorName() + " failed");
                throw new RuntimeException("Detect " + detector.getDetectorName() + " Failed");
            }
            if(detectResult.isPass()) {
                logger.info("Detect " + detector.getDetectorName() + " success");
            }
        }
    }

    public void setDetectorManager(DetectorManager detectorManager) {
        this.detectorManager = detectorManager;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
