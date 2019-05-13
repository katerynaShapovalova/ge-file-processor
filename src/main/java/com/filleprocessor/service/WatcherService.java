package com.filleprocessor.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

@Service
public class WatcherService implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(WatcherService.class);
    private final String DIR = "C:/temp777";

    private final WatchService watcher;
    private final FileProcessorService processorService;

    public WatcherService(FileProcessorService processorService) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.processorService = processorService;
        registerDirectory(Paths.get(DIR));
    }

    @PostConstruct
    public void checkDirectory() {
        while (true) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException ex) {
                return;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fileName = ev.context();

                processorService.processFile(fileName.toString());
            }

            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    @Override
    public void run() {
        checkDirectory();
    }

    private void registerDirectory(Path dir) throws IOException {
        dir.register(watcher, ENTRY_CREATE);
        LOGGER.info("Watch Service registered for dir: {}", dir.getFileName());
    }
}