package com.filleprocessor.model;

import java.io.Serializable;

public class ProcessingEvent implements Serializable{

    private EventType event;
    private String message;
    private String fileName;
    private Long timestamp;
    private String topic;

    public ProcessingEvent(EventType event, String message, String fileName, Long timestamp, String topic) {
        this.event = event;
        this.message = message;
        this.fileName = fileName;
        this.timestamp = timestamp;
        this.topic = topic;
    }

    public EventType getEvent() {
        return event;
    }

    public void setEvent(EventType event) {
        this.event = event;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
