package pers.lee.common.rpc.ci.log;

import java.util.List;

/**
 * LogService
 *
 * @author Drizzt Yang
 */
public interface LogService {

    List<String> listLogs();

    List<String> tail(String logName, Integer size);

    int lines(String logName);

    boolean isAppend(String logName, Integer lineNumber);

    List<String> next(String logName, Integer lineNumber, Integer size);

    List<String> find(String logName, String pattern, Integer size);

    int findLines(String logName, String pattern);

    List<String> findNext(String logName, String pattern, Integer lineNumber, Integer size);

    int findLines(String logName, List<String> pattern);

    List<String> findNext(String logName, List<String> pattern, Integer lineNumber, Integer size);
}
