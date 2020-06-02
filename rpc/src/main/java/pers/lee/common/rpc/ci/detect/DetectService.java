package pers.lee.common.rpc.ci.detect;

import java.util.List;
import java.util.Set;

/**
 * DetectService
 *
 * @author Drizzt Yang
 */
public interface DetectService {
    List<DetectResult> detect();

    List<DetectResult> detectOnly(Set<String> detectorNames);
}
