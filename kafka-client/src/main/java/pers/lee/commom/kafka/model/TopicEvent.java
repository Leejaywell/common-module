package pers.lee.commom.kafka.model;

import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @author: Jay.Lee
 * @date: 2018/11/28
 */
public class TopicEvent extends ApplicationEvent {
    private String key;
    private List<String> topics;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public TopicEvent(Object source, String key, List<String> topics) {
        super(source);
        this.key = key;
        this.topics = topics;
    }

    public List<String> getTopics() {
        return topics;
    }


    public String getKey() {
        return key;
    }

}
