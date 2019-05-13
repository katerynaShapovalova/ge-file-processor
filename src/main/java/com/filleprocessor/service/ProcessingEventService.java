package com.filleprocessor.service;

import com.filleprocessor.model.EventType;
import com.filleprocessor.model.ProcessingEvent;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.lang.System.nanoTime;

@Service
public class ProcessingEventService {

    public ProcessingEvent getProcessedEvent(Future<RecordMetadata> future, EventType eventType, String fileName, String topic) {

        RecordMetadata recordMetadata = null;
        try {
            recordMetadata = future.get();
            return createProcessedEvent(recordMetadata, eventType, fileName, topic, null);
        } catch (final InterruptedException | ExecutionException ex) {
            return createProcessedEvent(recordMetadata, eventType, fileName, topic, ex.getMessage());
        } catch (Exception ex) {
            return createProcessedEvent(recordMetadata, eventType, fileName, topic, ex.getMessage());
        }
    }

    private ProcessingEvent createProcessedEvent(RecordMetadata recordMetadata, EventType eventType, String fileName, String topic, String error) {

        if (recordMetadata != null && error == null) {
            return new ProcessingEvent(eventType,
                    String.format("Success sending record to topic - %s", recordMetadata.topic()),
                    fileName, nanoTime(), topic);
        } else {
            return new ProcessingEvent(eventType,
                    String.format("Error sending record to kafka topic - %s", error),
                    fileName, nanoTime(), topic);
        }
    }
}
