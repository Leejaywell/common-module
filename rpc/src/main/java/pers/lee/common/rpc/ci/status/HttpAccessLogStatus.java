package pers.lee.common.rpc.ci.status;

import pers.lee.common.rpc.utils.CommandExecutor;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * HttpAccessLogStatus
 *
 * @author Drizzt Yang
 */
public class HttpAccessLogStatus implements StatusAware {
    public static final List<Character> CHARACTERS = Arrays.asList('0', '1', '2', '3', '4');

    private static final Set<String> KEYS = new HashSet<String>();
    static {
        KEYS.add("1minute.total");
        KEYS.add("5minute.total");
        KEYS.add("1hour.total");
        //KEYS.add("all.total");

        KEYS.add("1minute.");
        KEYS.add("5minute.");
        KEYS.add("1hour.");
        //KEYS.add("all.");
    }

    private CommandExecutor commandExecutor = new CommandExecutor();
    private String logFilePath;
    private String name;

    private int locationIndex = 2;

    private int statusCodeIndex = 3;

    public HttpAccessLogStatus() {
    }

    public HttpAccessLogStatus(String name, String logFilePath) {
        this.name = name;
        this.logFilePath = logFilePath;
    }

    public HttpAccessLogStatus(String name, String logFilePath, int locationIndex, int statusCodeIndex) {
        this.name = name;
        this.logFilePath = logFilePath;
        this.locationIndex = locationIndex;
        this.statusCodeIndex = statusCodeIndex;
    }

    @Override
    public String getStatusPrefix() {
        return name;
    }

    @Override
    public Set<String> getStatusKeys() {
        return KEYS;
    }

    @Override
    public Map<String, String> status() {
        Map<String, String> status = new LinkedHashMap<String, String>();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        String timeString = simpleDateFormat.format(new Date());


        status.putAll(getStatus(timeString, "1minute."));

        if(CHARACTERS.contains(new Character(timeString.charAt(timeString.length() - 1)))) {
            timeString = timeString.substring(0, timeString.length() - 1) + "[0-4]";
        } else {
            timeString = timeString.substring(0, timeString.length() - 1) + "[5-9]";
        }
        status.putAll(getStatus(timeString, "5minute."));

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH");
        timeString = simpleDateFormat.format(new Date());
        status.putAll(getStatus(timeString, "1hour."));

        //status.putAll(getStatus(null, "all."));

        return status;
    }

    private Map<String, String> getStatus(String timeString, String prefix) {
        Map<String, String> status = new LinkedHashMap<String, String>();

        StringBuilder stringBuilder = new StringBuilder();
        if(timeString == null) {
            stringBuilder.append("sed 's/\"\\([^\"]*\\)\"//' ").append(logFilePath);
        } else {
            stringBuilder.append("grep -E '").append(timeString).append("' ").append(logFilePath)
                    .append(" | ").append("sed 's/\"\\([^\"]*\\)\"//'");
        }
        stringBuilder.append(" | ").append("awk '{print $").append(locationIndex).append(" \" \" $").append(statusCodeIndex).append("}'")
                .append(" | ").append("sort | uniq -c");

        List<List<String>> cols = commandExecutor.result2Cols(commandExecutor.execute(stringBuilder.toString()), null, 3);
        if(cols.size() == 0) {
            status.put(prefix + "total", "0");
            return status;
        }
        int total = 0;
        Map<String, Integer> locationCounts = new HashMap<String, Integer>();
        for (List<String> attributes : cols) {
            String location = attributes.get(1);
            location = location.replaceAll("\\.", "_");
            String statusCode = attributes.get(2);
            int count = Integer.parseInt(attributes.get(0));
            status.put(prefix + location + "." + statusCode, attributes.get(0));

            if(locationCounts.get(location) == null) {
                locationCounts.put(location, 0);
            }
            locationCounts.put(location, locationCounts.get(location) + count);
            total = total + count;
        }
        for (Map.Entry<String, Integer> entry: locationCounts.entrySet()) {
            status.put(prefix + entry.getKey(), String.valueOf(entry.getValue()));
        }
        return status;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}