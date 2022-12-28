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

package com.avispa.parser.precedence.grammar;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class Terminal extends Symbol {
    public static final Terminal BOUNDARY_MARKER = Terminal.of("MARKER", "\\$");

    @EqualsAndHashCode.Exclude
    private final Pattern pattern;

    public static Terminal of(String name, String regex) {
        return new Terminal(name, regex);
    }

    public static boolean isOf(Symbol symbol) {
        return symbol instanceof Terminal;
    }

    private Terminal(String name, String regex) {
        super(name);
        try {
            this.pattern = Pattern.compile("^" + regex);
        } catch (PatternSyntaxException e) {
            String message = String.format("Provided pattern is not a valid regular expression: %s", e.getMessage());
            log.error("Original exception: ", e);
            throw new IllegalStateException(message);
        }
    }

    public int lastMatchedIndex(String value) {
        Matcher matcher = pattern.matcher(value);
        return matcher.find() ?  matcher.end() : 0;
    }
}
