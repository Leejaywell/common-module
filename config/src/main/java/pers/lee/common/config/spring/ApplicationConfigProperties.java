package pers.lee.common.config.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by Jay.Lee on 2019/11/22 18:09
 */
@ConfigurationProperties(prefix = ApplicationConfigProperties.PREFIX)
public class ApplicationConfigProperties {

    public static final String PREFIX = "go.config";

    private Boolean autoRefresh = false;
    private ConfigKeeper configKeeper;


    public Boolean getAutoRefresh() {
        return autoRefresh;
    }

    public void setAutoRefresh(Boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
    }

    public ConfigKeeper getConfigKeeper() {
        return configKeeper;
    }

    public void setConfigKeeper(ConfigKeeper configKeeper) {
        this.configKeeper = configKeeper;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ApplicationConfigProperties{");
        sb.append("autoRefresh=").append(autoRefresh);
        sb.append(", configKeeper=").append(configKeeper);
        sb.append('}');
        return sb.toString();
    }

    public static class ConfigKeeper {
        private boolean enable = true;
        private String url;
        private String source;
        private String retryTime;
        private String pollTimeout;
        private boolean overrideNone = false;
        private boolean allowOverride = true;
        private boolean overrideSystemProperties = true;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getRetryTime() {
            return retryTime;
        }

        public void setRetryTime(String retryTime) {
            this.retryTime = retryTime;
        }

        public String getPollTimeout() {
            return pollTimeout;
        }

        public void setPollTimeout(String pollTimeout) {
            this.pollTimeout = pollTimeout;
        }

        public boolean isOverrideNone() {
            return overrideNone;
        }

        public void setOverrideNone(boolean overrideNone) {
            this.overrideNone = overrideNone;
        }

        public boolean isAllowOverride() {
            return allowOverride;
        }

        public void setAllowOverride(boolean allowOverride) {
            this.allowOverride = allowOverride;
        }

        public boolean isOverrideSystemProperties() {
            return overrideSystemProperties;
        }

        public void setOverrideSystemProperties(boolean overrideSystemProperties) {
            this.overrideSystemProperties = overrideSystemProperties;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("ConfigKeeper{");
            sb.append("enable=").append(enable);
            sb.append(", url='").append(url).append('\'');
            sb.append(", source='").append(source).append('\'');
            sb.append(", retryTime='").append(retryTime).append('\'');
            sb.append(", pollTimeout='").append(pollTimeout).append('\'');
            sb.append(", overrideNone=").append(overrideNone);
            sb.append(", allowOverride=").append(allowOverride);
            sb.append(", overrideSystemProperties=").append(overrideSystemProperties);
            sb.append('}');
            return sb.toString();
        }
    }

}
