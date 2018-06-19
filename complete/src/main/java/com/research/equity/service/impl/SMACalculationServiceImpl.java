package com.research.equity.service.impl;

import com.research.equity.fileloader.FileProcessingHelper;
import com.research.equity.service.ISMACalculationService;
import com.research.equity.task.FileProcessor;
import com.research.equity.utils.FileConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@Service
public class SMACalculationServiceImpl implements ISMACalculationService {

    @Value(value = "${input.file.location}")
    private String inputFileLocation;

    @Value(value = "${output.file.location}")
    private String outputFileLocation;

    @Value(value = "${top}")
    private int resultLimit;

    @Value(value = "${threshold}")
    private int threshold;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    FileProcessingHelper flh = new FileProcessingHelper();

    @Override
    public void runSMACalculationJob() {
        File[] files = retrieveFiles(inputFileLocation);
        FileProcessor fileProcessingTask = new FileProcessor(files, resultLimit, threshold);
        ForkJoinPool pool = ForkJoinPool.commonPool();
        pool.execute(fileProcessingTask);
        List<Map<String, Object>> outputData = fileProcessingTask.join();

        outputData = prepareFinalList(outputData);


        flh.writeResultFile(outputFileLocation, outputData);

    }

    private List<Map<String, Object>> prepareFinalList(List<Map<String, Object>> outputData) {
        //Prepare the final list
        outputData.sort((r1, r2) -> Double.compare((Double) r2.get(FileConstants.SMA), (Double) r1.get(FileConstants.SMA)));
        return outputData.stream().limit(resultLimit).collect(Collectors.toList());
    }

    public File[] retrieveFiles(String inputFileLocation) {
        File dir = new File(inputFileLocation);
        File[] directoryListing = dir.listFiles();
        try {
            if (directoryListing != null) {
                return directoryListing;
            } else {
                throw new RuntimeException("!!! No files available on the specified location.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
