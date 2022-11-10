package com.avispa.precedence_parsers.shunting_yard.token;

/**
 * @author Rafał Hiszpański
 */
public class Operand implements Token {
    private final String value;

    public Operand(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
