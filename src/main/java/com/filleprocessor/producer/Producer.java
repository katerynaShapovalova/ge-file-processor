package com.filleprocessor.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.Future;

@Service
public class Producer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);
    private KafkaProducer<String, String> kafkaProducer;

    public Producer(KafkaProducer<String, String> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public Future sendToKafka(Object o, String topic) {
        String json = null;
        try {
            json = new ObjectMapper().writeValueAsString(o);
        } catch (IOException e) {
            LOGGER.info("ERROR send during object serializing process for topic %s = %s", topic, e.getMessage());
        }

        LOGGER.info(String.format("#### -> Producing message -> %s, topic = %s", json, topic));
        return this.kafkaProducer.send(new ProducerRecord<>(topic, json));
    }
}
