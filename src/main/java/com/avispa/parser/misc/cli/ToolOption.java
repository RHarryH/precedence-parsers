package com.avispa.parser.misc.cli;

import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Rafał Hiszpański
 */
@Getter
public enum ToolOption {
    INPUT("i", "input", true, "Input expression for shunting-yard algorithm"),
    OUTPUT("o", "output", true, String.format("Output mode. Possible values are: %s", getOptionsList())),
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
