package com.avispa.parser.misc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Prints truth-table like table provided in form of map of non-integer indices and value. If cell does not
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

    private final String[] horizontalHeader;
    private final String[] verticalHeader;
    private final Map<Pair<String, String>, String> data;

    private final int columns;
    private final int columnWidth;
    private final String newLine;

    /**
     * Comparator used to order tokens using following rules:
     * - non-terminals (written in lower case) are always before terminals (upper case)
     * - boundary marker ($) is always last
     * - non-terminals and terminals (except boundary marker) are sorted by string value within their group
     * @return
     */
    private static Comparator<String> tokenComparator() {
        return (a, b) -> {
            if(Character.isUpperCase(a.charAt(0)) && Character.isLowerCase(b.charAt(0))) {
                return 1;
            } else if (Character.isLowerCase(a.charAt(0)) && Character.isUpperCase(b.charAt(0))) {
                return -1;
            } else {
                final String marker = "MARKER";
                // move marker token always to the end
                if (a.equals(marker) && !b.equals(marker)) {
                    return 1;
                } else if (!a.equals(marker) && b.equals(marker)) {
                    return -1;
                } else {
                    return a.compareTo(b);
                }
            }
        };
    }

    public TablePrinter(Map<Pair<String, String>, String> data) {
        this.data = data;

        this.verticalHeader = getHeader(data, Pair::getLeft);
        this.horizontalHeader = getHeader(data, Pair::getRight);

        this.columns = this.horizontalHeader.length + 1;
        this.columnWidth = getLongestHeaderValue(this.horizontalHeader) + 2;
        this.newLine = System.lineSeparator();
    }

    private String[] getHeader(Map<Pair<String, String>, String> data, Function<Pair<String, String>, String> mapper) {
        return data.keySet().stream().map(mapper)
                .distinct()
                .filter(StringUtils::isNotBlank)
                .sorted(tokenComparator())
                .toArray(String[]::new);
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

        if(horizontalHeader.length == 0 || verticalHeader.length == 0) {
            log.warn("Horizontal or vertical header is empty. Nothing will be printed");
            return "";
        }

        appendHorizontalHeader(sb);
        appendData(sb);

        return sb.toString();
    }

    private void appendHorizontalHeader(StringBuilder sb) {
        sb.append(getFirstSeparator()); // initial separator

        sb.append(getEmptyCell());
        for (String row : horizontalHeader) {
            sb.append(getCell(row));
        }
        sb.append(COLUMNS_SEPARATOR).append(newLine);
    }

    private void appendData(StringBuilder sb) {
        sb.append(getSeparator());
        for(int rowIndex = 0; rowIndex < verticalHeader.length; rowIndex++) {
            String row = verticalHeader[rowIndex];

            sb.append(getCell(row));
            for (String column : horizontalHeader) {
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
        if(rowIndex < verticalHeader.length - 1){
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
