package com.avispa.parser.misc;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
class TablePrinterTest {
    private static final String NEW_LINE = System.lineSeparator();

    @Test
    void givenEmptyData_whenPrint_thenPrintNothing() {
        // given
        Map<Pair<String, String>, String> data = new HashMap<>();

        // when
        String table = new TablePrinter(data).print();

        // then
        assertEquals("", table);
    }

    @Test
    void givenData_whenPrint_thenTableContainsData() {
        // given
        Map<Pair<String, String>, String> data = new HashMap<>();
        data.put(Pair.of("X", "X"), "0");
        data.put(Pair.of("X", "Y"), "1");
        data.put(Pair.of("Y", "X"), "2");

        // when
        String table = new TablePrinter(data).print();

        // then
        final String expected =
                "┌───┬───┬───┐" + NEW_LINE +
                "│   │ X │ Y │" + NEW_LINE +
                "├───┼───┼───┤" + NEW_LINE +
                "│ X │ 0 │ 1 │" + NEW_LINE +
                "├───┼───┼───┤" + NEW_LINE +
                "│ Y │ 2 │   │" + NEW_LINE +
                "└───┴───┴───┘" + NEW_LINE;

        assertEquals(expected, table);
    }

    @Test
    void givenDataWithUnevenHeaders_whenPrint_thenProperlyDetectedHorizontalAndVerticalHeader() {
        // given
        Map<Pair<String, String>, String> data = new HashMap<>();
        data.put(Pair.of("X", "Z"), "0");
        data.put(Pair.of("X", "Y"), "1");
        data.put(Pair.of("Y", "X"), "2");

        // when
        String table = new TablePrinter(data).print();

        // then
        final String expected =
                "┌───┬───┬───┬───┐" + NEW_LINE +
                "│   │ X │ Y │ Z │" + NEW_LINE +
                "├───┼───┼───┼───┤" + NEW_LINE +
                "│ X │   │ 1 │ 0 │" + NEW_LINE +
                "├───┼───┼───┼───┤" + NEW_LINE +
                "│ Y │ 2 │   │   │" + NEW_LINE +
                "└───┴───┴───┴───┘" + NEW_LINE;

        assertEquals(expected, table);
    }
}