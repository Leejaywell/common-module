package pers.lee.common.rpc.ci.status;

import pers.lee.common.lang.utils.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Passyt on 2017/8/9.
 */
public class IPStatus implements StatusAware {

    private static Logger log = LoggerFactory.getLogger(IPStatus.class);

    public static final String PREFIX_IP = "ip";
    public static final Set<String> STATUS_KEYS = new HashSet<String>();

    static {
        STATUS_KEYS.add("internal.");
        STATUS_KEYS.add("external.");
    }

    private IPStatus() {
    }

    @Override
    public String getStatusPrefix() {
        return PREFIX_IP;
    }

    @Override
    public Set<String> getStatusKeys() {
        return STATUS_KEYS;
    }

    @Override
    public Map<String, String> status() {
        return IPUtils.ipInfo();
    }

    private static IPStatus status = new IPStatus();

    public static IPStatus get() {
        return status;
    }

    public static void main(String... args) {
        System.out.println(new IPStatus().status());
    }

}
