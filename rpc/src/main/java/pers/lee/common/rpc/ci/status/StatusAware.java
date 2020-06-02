package pers.lee.common.rpc.ci.status;

import java.util.Map;
import java.util.Set;

/**
 * StatusAware
 *
 * @author Drizzt Yang
 */
public interface StatusAware {
    String getStatusPrefix();

    Set<String> getStatusKeys();

    Map<String, String> status();
}
