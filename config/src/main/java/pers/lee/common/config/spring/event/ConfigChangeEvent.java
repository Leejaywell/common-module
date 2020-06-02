package pers.lee.common.config.spring.event;

import org.springframework.context.ApplicationEvent;

/**
 * Created by Passyt on 2018/3/24.
 */
public class ConfigChangeEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1062598028324365676L;
    private final String key;
    private final String value;

    public ConfigChangeEvent(Object source, String key, String value) {
        super(source);
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

}
