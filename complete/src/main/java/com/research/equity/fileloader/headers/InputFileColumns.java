package com.research.equity.fileloader.headers;

import com.research.equity.utils.FileConstants;
import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.HashMap;
import java.util.Map;

public class InputFileColumns {


    public static final Map<String, CellProcessor> INPUT_FILE_COLS = new HashMap<String, CellProcessor>();

    static {
        INPUT_FILE_COLS.put(FileConstants.SYMBOL, new Optional());
        INPUT_FILE_COLS.put(FileConstants.SERIES, new Optional());
        INPUT_FILE_COLS.put(FileConstants.DATE, new Optional());
        INPUT_FILE_COLS.put(FileConstants.PREV_CLOSE, new Optional());
        INPUT_FILE_COLS.put(FileConstants.OPEN_PRICE, new Optional());
        INPUT_FILE_COLS.put(FileConstants.HIGH_PRICE, new Optional());
        INPUT_FILE_COLS.put(FileConstants.LOW_PRICE, new Optional());
        INPUT_FILE_COLS.put(FileConstants.LAST_PRICE, new Optional());
        INPUT_FILE_COLS.put(FileConstants.CLOSE_PRICE, new Optional());
        INPUT_FILE_COLS.put(FileConstants.AVERAGE_PRICE, new Optional());
        INPUT_FILE_COLS.put(FileConstants.TOTAL_TRADED_QUANTITY, new Optional());
        INPUT_FILE_COLS.put(FileConstants.TURNOVER, new Optional());
        INPUT_FILE_COLS.put(FileConstants.NO_OF_TRADES, new Optional());
        INPUT_FILE_COLS.put(FileConstants.DELIVERABLE_QTY, new Optional());
        INPUT_FILE_COLS.put(FileConstants.DLY_QT_TO_TRADED_QTY, new Optional());
    }


}
