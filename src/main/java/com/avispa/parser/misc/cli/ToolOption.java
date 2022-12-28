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

package com.avispa.parser.misc.cli;

import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Rafał Hiszpański
 */
@Getter
public enum ToolOption {
    INPUT("i", "input", true, "Input string for shunting-yard algorithm or parser (depending if grammar is provided)"),
    OUTPUT("o", "output", true, String.format("Output mode. Possible values are: %s", getOptionsList())),
    GRAMMAR("g", "grammar", true, "Grammar file"),
    START("s", "start-symbol", true, "Start symbol for provided grammar"),
    HELP("h", "help", false, "Prints this help details");

    private static String getOptionsList() {
        return Arrays.stream(OutputMode.values()).map(OutputMode::getName).collect(Collectors.joining(", "));
    }

    private final String name;
    private final String longName;
    private final boolean hasArguments;
    private final String description;

    ToolOption(String name, String longName, boolean hasArguments, String description) {
        this.name = name;
        this.longName = longName;
        this.hasArguments = hasArguments;
        this.description = description;
    }
}
