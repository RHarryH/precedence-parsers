package com.avispa.parser.misc.cli;

import lombok.Getter;

/**
 * @author Rafał Hiszpański
 */
@Getter
public enum OutputMode {
    TOKEN_LIST("token-list"),
    EVALUATED("evaluate"),
    EXPRESSION_TREE("expression-tree"),
    PARSE_TREE("parse-tree"),
    DERIVATION("derivation"),
    PRODUCTION_LIST("production-list"),
    REVERSE_POLISH_NOTATION("rpn");

    private final String name;

    OutputMode(String name) {
        this.name = name;
    }
}
