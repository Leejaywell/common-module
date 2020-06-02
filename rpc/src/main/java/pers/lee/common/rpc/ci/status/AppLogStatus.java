package pers.lee.common.rpc.ci.status;

import pers.lee.common.rpc.utils.CommandExecutor;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * AppLogStatus
 *
 * @author Drizzt Yang
 */
public class AppLogStatus implements StatusAware {
    private static final Set<String> KEYS = new HashSet<String>();

    static {
        KEYS.add("1minute.error");
        KEYS.add("5minute.error");
        KEYS.add("1hour.error");
        //KEYS.add("all.error");

        KEYS.add("1minute.warn");
        KEYS.add("5minute.warn");
        KEYS.add("1hour.warn");
        //KEYS.add("all.warn");

        KEYS.add("1minute.info");
        KEYS.add("5minute.info");
        KEYS.add("1hour.info");
        //KEYS.add("all.info");
    }

    public static final List<Character> CHARACTERS = Arrays.asList('0', '1', '2', '3', '4');

    private CommandExecutor commandExecutor = new CommandExecutor();
    private String logFilePath;
    private String name;


    public AppLogStatus() {
    }

    public AppLogStatus(String name, String logFilePath) {
        this.name = name;
        this.logFilePath = logFilePath;
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

        status.put("1minute.error", commandExecutor.execute(createCommand(timeString, "ERROR")));
        status.put("1minute.info", commandExecutor.execute(createCommand(timeString, "INFO")));
        status.put("1minute.warn", commandExecutor.execute(createCommand(timeString, "WARN")));


        if(CHARACTERS.contains(new Character(timeString.charAt(timeString.length() - 1)))) {
            timeString = timeString.substring(0, timeString.length() - 1) + "[0-4]";
        } else {
            timeString = timeString.substring(0, timeString.length() - 1) + "[5-9]";
        }
        status.put("5minute.error", commandExecutor.execute(createCommand(timeString, "ERROR")));
        status.put("5minute.info", commandExecutor.execute(createCommand(timeString, "INFO")));
        status.put("5minute.warn", commandExecutor.execute(createCommand(timeString, "WARN")));

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH");
        timeString = simpleDateFormat.format(new Date());
        status.put("1hour.error", commandExecutor.execute(createCommand(timeString, "ERROR")));
        status.put("1hour.info", commandExecutor.execute(createCommand(timeString, "INFO")));
        status.put("1hour.warn", commandExecutor.execute(createCommand(timeString, "WARN")));

//        status.put("all.error", commandExecutor.execute(createCommand(null, "ERROR")));
//        status.put("all.info", commandExecutor.execute(createCommand(null, "INFO")));
//        status.put("all.warn", commandExecutor.execute(createCommand(null, "WARN")));

        return status;
    }

    private String createCommand(String timeString, String level) {
        if(timeString == null) {
            return new StringBuilder("grep -c '").append(level).append("' ")
                    .append(logFilePath).toString();
        }
        return new StringBuilder("grep -E '").append(timeString).append("' ")
                .append(logFilePath).append(" | grep -c '").append(level).append("'").toString();
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
