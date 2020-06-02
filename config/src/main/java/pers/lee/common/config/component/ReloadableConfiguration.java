package pers.lee.common.config.component;

import pers.lee.common.config.Configuration;
import pers.lee.common.lang.properties.SortStringMap;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ReloadableConfiguration
 *
 * @author Drizzt Yang
 */
public abstract class ReloadableConfiguration extends BaseConfiguration implements Configuration {
    public static final Logger LOGGER = LoggerFactory.getLogger(ReloadableConfiguration.class);

    protected long timestamp = 0;
    protected ScheduledExecutorService executorService;
    protected boolean reloadActivated = false;

    public ReloadableConfiguration() {
    }

    public ReloadableConfiguration(String name, boolean reloadActivated) {
        super(name);
        this.reloadActivated = reloadActivated;
    }


    @Override
    public void init() {
        this.stringMap = new SortStringMap();
        this.stringMap.putAll(reload());
        if (reloadActivated) {
            executorService = new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder().namingPattern("timed.config." + getName() + ".reload").build());
            executorService.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    try {
                        ReloadableConfiguration reloadableConfiguration = ReloadableConfiguration.this;
                        if (reloadableConfiguration.getCurrentTimestamp() > reloadableConfiguration.getTimestamp()) {
                            reloadableConfiguration.applyReload();
                            LOGGER.info("config " + getName() + " has been reloaded, current " + reloadableConfiguration.getCurrentTimestamp());
                        } else {
                            LOGGER.debug("config " + getName() + " is not changed");
                        }
                    } catch (Exception e) {
                        LOGGER.error("config " + getName() + " reloading failed", e);
                    }
                }
            }, 1, 1, TimeUnit.MINUTES);
        }
        fireInit();
    }

    @Override
    public void destroy() {
        try {
            executorService.shutdown();
        } catch (Exception ignored) {
        }
    }

    public long getTimestamp() {
        return timestamp;
    }

    protected void applyReload() {
        Map<String, String> sortStringMap = this.reload();
        Set<String> keys = new HashSet<>(this.getKeys());
        for (String key : keys) {
            if (sortStringMap.containsKey(key)) {
                if (!sortStringMap.get(key).equals(this.getString(key))) {
                    this.stringMap.put(key, sortStringMap.get(key));
                    this.fireChanges(key);
                }
                sortStringMap.remove(key);
            } else {
                this.stringMap.remove(key);
                this.fireChanges(key);
            }
        }
        for (Map.Entry entry : sortStringMap.entrySet()) {
            String key = entry.getKey().toString();
            this.stringMap.put(key, entry.getValue().toString());
            this.fireChanges(key);
        }
    }

    protected abstract long getCurrentTimestamp();

    protected abstract Map<String, String> reload();
}
