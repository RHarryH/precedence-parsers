package com.avispa.precedence_parsers.shunting_yard.token;

import java.math.BigDecimal;

/**
 * @author Rafał Hiszpański
 */
public class Operand implements Token {
    private final BigDecimal value;

    public static Operand from(String value) {
        return new Operand(value);
    }

    public static Operand from(BigDecimal value) {
        return new Operand(value);
    }

    private Operand(String value) {
        this.value = new BigDecimal(value);
    }

    private Operand(BigDecimal value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value.toPlainString();
    }

    public BigDecimal get() {
        return value;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
