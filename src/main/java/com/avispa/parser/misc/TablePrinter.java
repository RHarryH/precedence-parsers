package com.avispa.parser.misc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Prints truth-table like table provided in form of map of non-integer indices and value. It requires providing
 * separate list of possible values called header as they are printed as horizontal and vertical header. If cell does not
 * have corresponding value in data then empty value is inserted. Example:
 * Header: X Y
 * Data:
 * (X, X) -> 0
 * (X, Y) -> 1
 * (Y, X) -> 2
 * Result:
 * ┌───┬───────┐
 * │   │ X │ Y │
 * ├───┼───┼───┤
 * │ X │ 0 │ 1 │
 * ├───┼───┼───┤
 * │ Y │ 2 │   │
 * └───┴───┴───┘
 * @author Rafał Hiszpański
 */
@Slf4j
public class TablePrinter {
    private static final String COLUMNS_SEPARATOR = "│";
    public static final String ROW_SEPARATOR = "─";
    public static final String EMPTY_VALUE = " ";

    private final String[] header;
    private final Map<Pair<String, String>, String> data;

    private final int columns;
    private final int columnWidth;
    private final String newLine;

    public TablePrinter(String[] header, Map<Pair<String, String>, String> data) {
        this.header = header;
        this.data = data;

        this.columns = header.length + 1;
        this.columnWidth = getLongestHeaderValue(header) + 2;
        this.newLine = System.lineSeparator();
    }

    /**
     * Gets longest value on header list
     * @param header
     * @return
     */
    private int getLongestHeaderValue(String[] header) {
        return Stream.of(header)
                .map(String::length)
                .max(Integer::compareTo)
                .orElse(0);
    }

    public String print() {
        StringBuilder sb = new StringBuilder();

        if(header.length == 0) {
            log.warn("Header is empty. Nothing will be printed");
            return "";
        }

        appendHeader(sb);
        appendData(sb);

        return sb.toString();
    }

    private void appendHeader(StringBuilder sb) {
        sb.append(getFirstSeparator()); // initial separator

        sb.append(getEmptyCell());
        for (String row : header) {
            sb.append(getCell(row));
        }
        sb.append(COLUMNS_SEPARATOR).append(newLine);
    }

    private void appendData(StringBuilder sb) {
        sb.append(getSeparator());
        for(int rowIndex = 0; rowIndex < header.length; rowIndex++) {
            String row = header[rowIndex];

            sb.append(getCell(row));
            for (String column : header) {
                appendCell(sb, row, column);
            }
            sb.append(COLUMNS_SEPARATOR).append(newLine);

            appendDataSeparator(sb, rowIndex);
        }
    }

    private void appendCell(StringBuilder sb, String row, String column) {
        Pair<String, String> pair = Pair.of(row, column);
        if (data.containsKey(pair)) {
            sb.append(getCell(data.get(pair)));
        } else {
            sb.append(getEmptyCell());
        }
    }

    private void appendDataSeparator(StringBuilder sb, int rowIndex) {
        if(rowIndex < header.length - 1){
            sb.append(getSeparator());
        } else {
            sb.append(getLastSeparator());
        }
    }

    /**
     * Prints separator, which starts the table
     * @return
     */
    private String getFirstSeparator() {
        return getSeparator("┌", "┬", "┐");
    }

    /**
     * Prints separator, which ends the table
     * @return
     */
    private String getLastSeparator() {
        return getSeparator("└", "┴", "┘");
    }

    /**
     * Prints rows' separator
     * @return
     */
    private String getSeparator() {
        return getSeparator("├", "┼", "┤");
    }

    private String getSeparator(String left, String center, String right) {
        StringBuilder sb = new StringBuilder(left);

        for(int i = 0; i < columns; i++) {
            sb.append(ROW_SEPARATOR.repeat(columnWidth));
            if(i < columns - 1) {
                sb.append(center);
            } else {
                sb.append(right);
            }
        }

        return sb.append(newLine).toString();
    }

    private String getEmptyCell() {
        return COLUMNS_SEPARATOR + EMPTY_VALUE.repeat(columnWidth);
    }

    private String getCell(String value) {
        return COLUMNS_SEPARATOR + StringUtils.center(value, columnWidth);
    }
}
