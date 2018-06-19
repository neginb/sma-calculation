package com.research.equity.fileloader;


import com.research.equity.fileloader.headers.InputFileColumns;
import com.research.equity.fileloader.headers.OutputFileColumns;
import com.research.equity.utils.FileConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileProcessingHelper {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final CsvPreference CSV_PREF = new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).build();

    /**
     * Assign CellProcessor to each of the required columns.
     *
     * @param headers         - array containing all the headers from the source files.
     * @param requiredColumns - set of required headers.
     * @return CellProcessor array - instruction on how each cell should be processed.
     */
    private CellProcessor[] getCellProcessors(String[] headers, Map<String, CellProcessor> requiredColumns) {
        CellProcessor[] processors = new CellProcessor[headers.length];
        for (int i = 0; i < headers.length; i++) {
            final CellProcessor processor = requiredColumns.get(headers[i]);
            if (processor != null) {
                processors[i] = processor;
            } else {
                headers[i] = null;
            }
        }
        return processors;
    }

    /**
     * Method to validate each row of the csv file and calculate sma.
     *
     * @param sourceFilePath
     * @param requiredCols
     * @return the row with current day's data and calculated SMA.
     */
    private synchronized Map<String, Object> processCsvData(String sourceFilePath, Map<String, CellProcessor> requiredCols) {
        InputStream inputStream = null;
        List<Map<String, Object>> rows = new ArrayList<>();
        double sma = 0;
        logger.error("***************** File : {} ***************", sourceFilePath);
        try {
            File file = new File(sourceFilePath);
            inputStream = new FileInputStream(sourceFilePath);
            ICsvMapReader csvReader = new CsvMapReader(new InputStreamReader(inputStream), CSV_PREF);
            String[] headers = csvReader.getHeader(true);

            final CellProcessor[] processors = getCellProcessors(headers, requiredCols);

            if (headers != null) {
                Map<String, Object> row;
                while ((row = csvReader.read(headers, processors)) != null) {
                    String date = row.get(FileConstants.DATE) != null ? ((String) row.get(FileConstants.DATE)).trim() : null;

                    if (date != null && isDateWithinRange(date)) {
                        rows.add(row);
                    }
                }
            } else {
                logger.info("Cannot proceed with file processing.");
                csvReader.close();
                inputStream.close();
                throw new RuntimeException("Invalid input file");
            }
        } catch (IOException ex) {
            logger.error("Error occurred while processing the file.");
        } catch (Exception ex) {
            logger.error("Error occurred while processing file : {} - Exception : {}", sourceFilePath, ex);
        }
        if (rows.isEmpty()) {
            return null;
        }

        // Calculating SMA
        DoubleSummaryStatistics stats = rows.stream()
                .mapToDouble((x) -> Double.parseDouble(((String) x.get(FileConstants.CLOSE_PRICE)).trim()))
                .summaryStatistics();

        Map<String, Object> resultRow = rows.get(0);
        resultRow.put(FileConstants.SMA, stats.getAverage());
        return resultRow;
    }

    /**
     * Method to check if the specified date is within 2 years range.
     *
     * @param date
     * @return
     */
    public boolean isDateWithinRange(String date) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

        LocalDate dateToCheck = LocalDate.parse(date, formatter);
        LocalDate today = LocalDate.now();
        LocalDate twoYearsBefore = LocalDate.now().minusYears(2);
        return dateToCheck.isAfter(twoYearsBefore);
    }

    public synchronized List<Map<String, Object>> processFiles(File[] files) {

        List<Map<String, Object>> outputData = new ArrayList<>();
        try {
            for (int i = 0; i < files.length; i++) {
                Map<String, Object> resultRow = processCsvData(files[i].getAbsolutePath(), InputFileColumns.INPUT_FILE_COLS);

                if (resultRow != null && isClosingPriceLowerThanSMA(resultRow)) {
                    outputData.add(resultRow);
                }
            }
            outputData.sort((r1, r2) -> Double.compare((Double) r2.get(FileConstants.SMA), (Double) r1.get(FileConstants.SMA)));

        } catch (Exception ex) {
            logger.error("Error occurred while writing the final file : {}", ex);
        }
        return outputData;
    }

    /**
     * Check if the current day's closing price is less than calculated SMA.
     *
     * @param resultRow
     * @return
     */
    private boolean isClosingPriceLowerThanSMA(Map<String, Object> resultRow){
        return (Double.parseDouble((String) resultRow.get(FileConstants.CLOSE_PRICE))) < ((Double) resultRow.get(FileConstants.SMA));
    }

    public void writeResultFile(String outputFilePath, List<Map<String, Object>> outputData) {
        String[] outputHeaders = OutputFileColumns.OUTPUT_FILE_COLS.keySet().toArray(new String[OutputFileColumns.OUTPUT_FILE_COLS.keySet().size()]);
        CellProcessor[] cellProcessors = OutputFileColumns.OUTPUT_FILE_COLS.values().toArray(new CellProcessor[OutputFileColumns.OUTPUT_FILE_COLS.values().size()]);

        try {
            writeFinalExtract(outputFilePath, outputHeaders, cellProcessors, outputData);
        } catch (IOException e) {
            logger.error("Error occurred while writing resultant file : {}", e);
        }
    }

    private void writeFinalExtract(String fullFileNamePath, String[] header, CellProcessor[] cellProcessors, List<Map<String, Object>> outputRows) throws IOException {
        ICsvMapWriter mapWriter = null;
        try {
            mapWriter = new CsvMapWriter(new FileWriter(fullFileNamePath), CsvPreference.STANDARD_PREFERENCE);
            mapWriter.writeHeader(header);

            for (Map<String, Object> r : outputRows) {
                mapWriter.write(r, header);
            }
        } finally {
            if (mapWriter != null) {
                mapWriter.close();
            }
        }
    }
}
