package com.filleprocessor.model;

public enum EventType {
    CREATED("Created"),
    PROCESSED("Processed");

    private String value;
    EventType(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
