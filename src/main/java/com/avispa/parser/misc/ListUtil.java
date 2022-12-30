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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Rafał Hiszpański
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ListUtil {
    /**
     * Split list using sliding window of defined size.
     * Example:
     * - [A, B, C], size: 2 -> [A, B], [B, C]
     * - [A, B, C, D], size: 3 -> [A, B, C], [B, C, D]
     * @param list source list
     * @param size window size
     * @param <T>
     * @return stream containing list elements of specified size
     */
    public static <T> Stream<List<T>> sliding(List<T> list, int size) {
        if(size > list.size())
            return Stream.empty();
        return IntStream.range(0, list.size() - size + 1)
                .mapToObj(start -> list.subList(start, start + size));
    }

    /**
     * Checks if list ends with the specific suffix
     * @param list source list
     * @param suffix suffix
     * @return <code>true</code> if list ends with suffix
     */
    public static <T> boolean endsWith(List<T> list, List<T> suffix) {
        int idx = list.size() - suffix.size();

        if (idx < 0) {
            return false;
        }

        for (int i = 0; i < suffix.size(); i++) {
            if (!list.get(idx + i).equals(suffix.get(i))) {
                return false;
            }
        }

        return true;
    }
}
