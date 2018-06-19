package com.research.equity.fileloader.headers;

import com.research.equity.utils.FileConstants;
import org.springframework.util.StringUtils;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.HashMap;
import java.util.Map;

public class OutputFileColumns {

    public static final Map<String, CellProcessor> OUTPUT_FILE_COLS = new HashMap<String, CellProcessor>();

    static {
        OUTPUT_FILE_COLS.put(FileConstants.SYMBOL, new Optional());
        OUTPUT_FILE_COLS.put(FileConstants.CLOSE_PRICE, new Optional());
        OUTPUT_FILE_COLS.put(FileConstants.SMA, new Optional());
        OUTPUT_FILE_COLS.put(FileConstants.TOTAL_TRADED_QUANTITY, new Optional());
        OUTPUT_FILE_COLS.put(FileConstants.DELIVERABLE_QTY, new Optional());
    }
}
