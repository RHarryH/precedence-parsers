package com.avispa.precedence_parsers.shunting_yard.token;

import com.avispa.precedence_parsers.shunting_yard.OperatorAssociativity;
import lombok.Getter;

/**
 * Unary operators
 * @author Rafał Hiszpański
 */
@Getter
public enum UnaryOperator implements MathOperator {
    PLUS("+", 3, OperatorAssociativity.RIGHT),
    MINUS("-", 3, OperatorAssociativity.RIGHT);

    private final String value;
    private final int precedence;
    private final OperatorAssociativity associativity;

    UnaryOperator(final String value, final int precedence, final OperatorAssociativity associativity) {
        this.value = value;
        this.precedence = precedence;
        this.associativity = associativity;
    }

    public boolean isLeftAssociative() {
        return OperatorAssociativity.LEFT == this.associativity;
    }

    public boolean isRightAssociative() {
        return OperatorAssociativity.RIGHT == this.associativity;
    }
}
