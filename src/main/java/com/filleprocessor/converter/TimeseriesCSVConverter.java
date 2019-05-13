package com.filleprocessor.converter;

import com.filleprocessor.model.Timeseries;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class TimeseriesCSVConverter {

    final static String[] FIELDS = {"Guid", "Timestamp", "Type", "Value"};

    public List<Timeseries> getTimeseriesFromCsv(String path) {
        List<Timeseries> timeseries = new ArrayList<>();
        Iterator<Timeseries> timeseriesIterator = getBuilder(path).iterator();
        if (timeseriesIterator != null) {
            while (timeseriesIterator.hasNext()) {
                Timeseries record = timeseriesIterator.next();
                timeseries.add(record);
            }
        }
        return timeseries;
    }

    private ColumnPositionMappingStrategy getMappingStrategy() {
        ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
        strategy.setType(Timeseries.class);
        strategy.setColumnMapping(FIELDS);

        return strategy;
    }

    private CsvToBean<Timeseries> getBuilder(String path) {
        Reader reader = null;
        try {
            reader = Files.newBufferedReader(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        CsvToBean<Timeseries> csvToBean = null;
        if(reader != null) {
            csvToBean = new CsvToBeanBuilder(reader)
                    .withMappingStrategy(getMappingStrategy())
                    .withSkipLines(1)
                    .build();
        }
        return csvToBean;
    }
}
