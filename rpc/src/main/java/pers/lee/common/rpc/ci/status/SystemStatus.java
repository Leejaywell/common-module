package pers.lee.common.rpc.ci.status;

import pers.lee.common.rpc.utils.CommandExecutor;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

/**
 * SystemStatus
 *
 * @author Drizzt Yang
 */
public class SystemStatus implements StatusAware {
    private static final Logger logger = LoggerFactory.getLogger(SystemStatus.class);

    private static final Set<String> KEYS = new HashSet<String>();
    private static final Set<String> processNames = new HashSet<String>();

    static {
        KEYS.add("load.1minute");
        KEYS.add("load.5minute");
        KEYS.add("load.15minute");

        KEYS.add("cpu.user");
        KEYS.add("cpu.system");
        KEYS.add("cpu.nice");
        KEYS.add("cpu.idle");
        KEYS.add("cpu.iowait");
        KEYS.add("cpu.hardware.interrupt");
        KEYS.add("cpu.software.interrupt");
        KEYS.add("cpu.steal");

        KEYS.add("mem.total");
        KEYS.add("mem.used");
        KEYS.add("mem.free");
        KEYS.add("mem.buffer");

        KEYS.add("swap.total");
        KEYS.add("swap.used");
        KEYS.add("swap.free");
        KEYS.add("swap.cached");

        KEYS.add("process.total.");
        KEYS.add("process.mem.");
        KEYS.add("process.cpu.");

        KEYS.add("io.");

        KEYS.add("file.");

        processNames.add("httpd");
        processNames.add("mysqld");
        processNames.add("java");

        processNames.add("openfile");
    }

    CommandExecutor commandExecutor = new CommandExecutor();
    @Override
    public String getStatusPrefix() {
        return "system";
    }

    @Override
    public Set<String> getStatusKeys() {
        return Sets.newHashSet("load", "", "");
    }

    @Override
    public Map<String, String> status() {
        Map<String, String> status = new LinkedHashMap<String, String>();

        String result = commandExecutor.execute("top -bn 1");
        List<String> topStatus = Arrays.asList(result.split("\n"));
        String loadStatus = topStatus.get(0);
        if (loadStatus.contains("load")) {
            try {
                Object[] objects = loadStatus.substring(loadStatus.indexOf("load")).replaceAll("[a-zA-Z%:()]", "").split(",");
                status.put("load.1minute", objects[0].toString());
                status.put("load.5minute", objects[1].toString());
                status.put("load.15minute", objects[2].toString());
            } catch (Exception e) {
                logger.warn("get system load failed", e);
            }
        }

        List<String> cpuObjects = commandExecutor.resultLine2Values(result, "Cpu(s)");
        if (cpuObjects.size() == 8) {
            status.put("cpu.user", cpuObjects.get(0));
            status.put("cpu.system", cpuObjects.get(1));
            status.put("cpu.nice", cpuObjects.get(2));
            status.put("cpu.idle", cpuObjects.get(3));
            status.put("cpu.iowait", cpuObjects.get(4));
            status.put("cpu.hardware.interrupt", cpuObjects.get(5));
            status.put("cpu.software.interrupt", cpuObjects.get(6));
            status.put("cpu.steal", cpuObjects.get(7));
        } else {
            logger.warn("get cpu failed");
        }

        List<String> memoryObjects = commandExecutor.resultLine2Values(result, "Mem");
        if (memoryObjects.size() == 4) {
            status.put("mem.total", memoryObjects.get(0));
            status.put("mem.used", memoryObjects.get(1));
            status.put("mem.free", memoryObjects.get(2));
            status.put("mem.buffer", memoryObjects.get(3));
        } else {
            logger.warn("get mem failed");
        }

        List<String> swapObjects = commandExecutor.resultLine2Values(result, "Swap");
        if (swapObjects.size() == 4) {
            status.put("swap.total", swapObjects.get(0));
            status.put("swap.used", swapObjects.get(1));
            status.put("swap.free", swapObjects.get(2));
            status.put("swap.cached", swapObjects.get(3));
        } else {
            logger.warn("get swap failed");
        }

        Map<String, Map<String, BigDecimal>> processStatusMap = new HashMap<String, Map<String, BigDecimal>>();
        for (String processLine : commandExecutor.result2Lines(result, "PID")) {
            List<String> processStatus = Arrays.asList(processLine.trim().split("\\s+"));
            String processName = processStatus.get(processStatus.size() - 1).trim();
            if(!processNames.contains(processName)) {
                continue;
            }

            Map<String, BigDecimal> attributes = processStatusMap.get(processName);
            if(attributes == null) {
                attributes = new HashMap<String, BigDecimal>();
                attributes.put("total", BigDecimal.ZERO);
                attributes.put("mem", BigDecimal.ZERO);
                attributes.put("cpu", BigDecimal.ZERO);
                processStatusMap.put(processName, attributes);
            }

            attributes.put("total", attributes.get("total").add(new BigDecimal(1)));
            attributes.put("cpu", attributes.get("cpu").add(new BigDecimal(processStatus.get(8).trim())));
            attributes.put("mem", attributes.get("mem").add(new BigDecimal(processStatus.get(9).trim())));
        }

        for (Map.Entry<String, Map<String, BigDecimal>> entry : processStatusMap.entrySet()) {
            String processName = entry.getKey();
            Map<String, BigDecimal> attributes = entry.getValue();
            status.put("process.total." + processName, attributes.get("total").toString());
            status.put("process.mem." + processName, attributes.get("mem").toString());
            status.put("process.cpu." + processName, attributes.get("cpu").toString());
        }

        List<List<String>> ioAttributes = commandExecutor.result2Cols(commandExecutor.execute("iostat -d -k"), "Device", 6);
        for (List<String> attributes : ioAttributes) {
            String device = attributes.get(0);
            status.put("io." + device + ".tps", attributes.get(1));
            status.put("io." + device + ".read.kBps", attributes.get(2));
            status.put("io." + device + ".write.kBps", attributes.get(3));
            status.put("io." + device + ".read.kB", attributes.get(4));
            status.put("io." + device + ".write.kB", attributes.get(5));
        }

        List<List<String>> fileAttributes = commandExecutor.result2Cols(commandExecutor.execute("df -k"), "File", 6);
        for (List<String> attributes : fileAttributes) {
            String device = attributes.get(0);
            status.put("file." + device + ".total.kB", attributes.get(1));
            status.put("file." + device + ".used.kB", attributes.get(2));
            status.put("file." + device + ".avail.kB", attributes.get(3));
            status.put("file." + device + ".used.percent", attributes.get(4));
            status.put("file." + device + ".mount", attributes.get(5));
        }

        List<List<String>> openFileStats = commandExecutor.result2Cols(commandExecutor.execute("lsof -n |awk '{print $1,$8}'|grep TCP|grep java|sort|uniq -c"), null, 1);
        if(openFileStats.size() > 0) {
            status.put("openfile.java.tcp", openFileStats.get(0).get(0));
        }

        openFileStats = commandExecutor.result2Cols(commandExecutor.execute("lsof -n |awk '{print $1}'|grep java|sort|uniq -c"), null, 1);
        if(openFileStats.size() > 0) {
            status.put("openfile.java.total", openFileStats.get(0).get(0));
        }

        //TODO: String netResult = commandExecutor.execute("netstat -s");
        return status;
    }

    private static SystemStatus systemStatus = new SystemStatus();

    public static SystemStatus get() {
        return systemStatus;
    }
}
