package com.filleprocessor.service;

import com.filleprocessor.converter.TimeseriesCSVConverter;
import com.filleprocessor.model.ProcessingEvent;
import com.filleprocessor.model.Timeseries;
import com.filleprocessor.producer.Producer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Future;

import static com.filleprocessor.model.EventType.CREATED;
import static com.filleprocessor.model.EventType.PROCESSED;
import static java.lang.System.nanoTime;
import static org.apache.commons.lang3.StringUtils.EMPTY;


@Service
public class FileProcessorService {

    private final TimeseriesCSVConverter converter;
    private final Producer producer;
    private final ProcessingEventService service;

    public FileProcessorService(TimeseriesCSVConverter converter, Producer producer, ProcessingEventService service) {
        this.converter = converter;
        this.producer = producer;
        this.service = service;
    }

    @Value("${watcher.directory}")
    private String dir;

    @Value("${notification.topic}")
    private String notificationTopic;

    @Value("${data.topic}")
    private String dataTopic;

    public void processFile(String fileName) {

        if (checkExtension(fileName)) {
            ProcessingEvent createdEvent = new ProcessingEvent(CREATED, EMPTY, fileName, nanoTime(), notificationTopic);

            producer.sendToKafka(createdEvent, notificationTopic);

            List<Timeseries> timeseries = converter.getTimeseriesFromCsv(dir + "/" + fileName);

            int size = timeseries.size();
            ProcessingEvent processedEvent;

            if (checkQuantity(size)) {
                Future<RecordMetadata> future = producer.sendToKafka(timeseries, dataTopic);
                processedEvent = service.getProcessedEvent(future, PROCESSED, fileName, dataTopic);
            } else {
                processedEvent = new ProcessingEvent(PROCESSED,
                        String.format("ERROR : message contain %s values", size), fileName, nanoTime(), dataTopic);
            }
            producer.sendToKafka(processedEvent, notificationTopic);
        }
    }

    private boolean checkExtension(String fileName) {
        return fileName.endsWith(".csv");
    }

    private boolean checkQuantity(int size) {
        return size > 0 && size <= 100;
    }
}
