package com.avispa.parser.precedence.table;

import lombok.Getter;

/**
 * @author Rafał Hiszpański
 */
@Getter
public enum Precedence {
    LESS_THAN("⋖"),
    LESS_THAN_OR_EQUALS("⩿"),
    EQUALS("≐"),
    GREATER_THAN("⋗");

    private final String symbol;

    Precedence(String symbol) {
        this.symbol = symbol;
    }
}
