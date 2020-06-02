package pers.lee.common.rpc.ci.log;

import pers.lee.common.config.Configuration;
import pers.lee.common.config.ConfigurationListener;
import pers.lee.common.lang.file.FileService;
import pers.lee.common.lang.file.IFileService;

import java.io.File;

/**
 * LogServiceFactory
 *
 * @author Drizzt Yang
 */
public class LogServiceFactory implements ConfigurationListener {

    private static LinuxLogService logService;
    private static IFileService logFileService;

    public static LogService get() {
        return logService;
    }

    public static IFileService getFileService() {
        return logFileService;
    }

    @Override
    public void notifyInit(Configuration configuration) {
        if (logService == null && configuration.getProperty("log.file.path") != null) {
            String fileDirectory = new File(configuration.getProperty("log.file.path")).getParent();
            logService = new LinuxLogService(fileDirectory);
            logFileService = new FileService(fileDirectory);
        }
    }

    @Override
    public void notifyUpdate(Configuration configuration, String key) {
        // not support update
    }
}
