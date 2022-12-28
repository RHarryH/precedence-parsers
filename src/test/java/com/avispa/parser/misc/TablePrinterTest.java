/*
 * MIT License
 *
 * Copyright (c) 2022 Rafał Hiszpański
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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