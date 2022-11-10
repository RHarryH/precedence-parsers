package com.avispa.precedence_parsers.shunting_yard.token;

import com.avispa.precedence_parsers.shunting_yard.OperatorAssociativity;
import lombok.Getter;

/**
 * Binary operators
 * @author Rafał Hiszpański
 */
@Getter
public enum BinaryOperator implements MathOperator {
    ADD("+", 1, OperatorAssociativity.LEFT),
    SUBTRACT("-", 1, OperatorAssociativity.LEFT),
    MULTIPLY("*", 2, OperatorAssociativity.LEFT),
    DIVIDE("/", 2, OperatorAssociativity.LEFT),
    POWER("^", 3, OperatorAssociativity.RIGHT);

    private final String value;
    private final int precedence;
    private final OperatorAssociativity associativity;

    BinaryOperator(final String value, final int precedence, final OperatorAssociativity associativity) {
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
