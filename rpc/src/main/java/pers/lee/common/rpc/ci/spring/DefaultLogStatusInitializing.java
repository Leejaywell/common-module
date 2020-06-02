package pers.lee.common.rpc.ci.spring;

import pers.lee.common.config.Configuration;
import pers.lee.common.config.ConfigurationListener;
import pers.lee.common.rpc.ci.status.AppLogStatus;
import pers.lee.common.rpc.ci.status.HttpAccessLogStatus;
import pers.lee.common.rpc.ci.status.StatusCenter;

import java.io.File;

/**
 * DefaultLogStatusInitializing
 *
 * @author Drizzt Yang
 */
public class DefaultLogStatusInitializing implements ConfigurationListener {
    public static final String APP_LOG = "applog";
    public static final String APACHE_HTTP = "apache-http";
    public static final String APACHE_HTTPS = "apache-https";
    public static final String HTTP_ACCESS = "http-access";
    protected StatusCenter statusCenter;

    @Override
    public void notifyInit(Configuration configuration) {
        statusCenter = StatusCenter.get();
        initAppLog(configuration);
        initHttpAccessLog(configuration);
    }

    protected void initAppLog(Configuration configuration) {
        String logFilePath = configuration.getString("log.file.path");
        if (logFilePath != null) {
            statusCenter.register(new AppLogStatus(APP_LOG, logFilePath));
            statusCenter.register(new HttpAccessLogStatus(APACHE_HTTP, new File(logFilePath).getParent() + "/noweb-access_log"));
            statusCenter.register(new HttpAccessLogStatus(APACHE_HTTPS, new File(logFilePath).getParent() + "/ssl-access_log"));
        }
    }

    protected void initHttpAccessLog(Configuration configurationn) {
        String accessLogFilePath = configurationn.getString("log.file.http.AccessLog.path");
        if (accessLogFilePath != null) {
            statusCenter.register(new HttpAccessLogStatus(HTTP_ACCESS, accessLogFilePath));
        }
    }

    @Override
    public void notifyUpdate(Configuration configuration, String key) {
        updateLogFilePath(key, configuration.getString(key));
        updateHttpAccessLogFilePath(key, configuration.getString(key));
    }

    protected void updateLogFilePath(String key, String logFilePath) {
        if (key.equals("log.file.path")) {
            AppLogStatus logStatus = (AppLogStatus) statusCenter.getStatusAware(APP_LOG);
            logStatus.setLogFilePath(logFilePath);
            HttpAccessLogStatus accessLogStatus;

            accessLogStatus = (HttpAccessLogStatus) statusCenter.getStatusAware(APACHE_HTTP);

            accessLogStatus.setLogFilePath(new File(logFilePath).getParent() + "/noweb-access_log");

            accessLogStatus = (HttpAccessLogStatus) statusCenter.getStatusAware(APACHE_HTTPS);
            accessLogStatus.setLogFilePath(new File(logFilePath).getParent() + "/ssl-access_log");
        }
    }
    
    protected void updateHttpAccessLogFilePath(String key, String accessLogFilePath) {
        if (key.equals("log.file.http.AccessLog.path")) {
            HttpAccessLogStatus accessLogStatus;

            accessLogStatus = (HttpAccessLogStatus) statusCenter.getStatusAware(HTTP_ACCESS);
            accessLogStatus.setLogFilePath(accessLogFilePath);
        }   
    }


}
