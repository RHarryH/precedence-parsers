package com.avispa.parser.misc;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Rafał Hiszpański
 */
class ListUtilTest {
    @Test
    void givenList_whenListSizeSmallerThanWindowSize_thenEmpty() {
        assertEquals(0, ListUtil.sliding(List.of("a"), 2).count());
    }

    @Test
    void givenList_whenListSizeEqualsWindowSize_thenSingleResult() {
        assertEquals(1, ListUtil.sliding(List.of("a", "b"), 2).count());
    }

    @Test
    void givenSingleElement_whenListSizeGreaterThanWindowSize_thenResultIsCorrect() {
        assertEquals(2, ListUtil.sliding(List.of("a", "b", "c"), 2).count());
    }
}