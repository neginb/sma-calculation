package com.research.equity.task;

import com.research.equity.fileloader.FileProcessingHelper;

import java.io.File;
import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class FileProcessor extends RecursiveTask<List<Map<String, Object>>> {

    FileProcessingHelper flh = new FileProcessingHelper();

    File[] files;
    int resultLimit;
    int threshold;

    public FileProcessor(File[] files, int resultLimit, int threshold) {
        this.files = files;
        this.resultLimit = resultLimit;
        this.threshold = threshold;
    }

    protected List<Map<String, Object>> computeDirectly(File[] files) {
        List<Map<String, Object>> resultList = flh.processFiles(files);

        if(resultList.size() > resultLimit){
            return resultList.stream().limit(resultLimit).collect(Collectors.toList());
        }
        return resultList;
    }

    @Override
    protected List<Map<String, Object>> compute() {
        if (files.length > threshold) {
            List<Map<String, Object>> result = new ArrayList<>();
            Collection<FileProcessor> subtasks = createSubtasks();

            subtasks.stream().forEach(subtask -> subtask.fork());
            subtasks.stream().forEach(subtask -> result.addAll(subtask.join()));

            return result;

        } else {
            return computeDirectly(files);
        }
    }

    private Collection<FileProcessor> createSubtasks() {
        List<FileProcessor> dividedTasks = new ArrayList<>();
        dividedTasks.add(new FileProcessor(Arrays.copyOfRange(files, 0, files.length / 2), resultLimit, threshold));
        dividedTasks.add(new FileProcessor(Arrays.copyOfRange(files, files.length / 2, files.length), resultLimit, threshold));
        return dividedTasks;
    }
}
