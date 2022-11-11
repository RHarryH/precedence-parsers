package com.avispa.precedence_parsers.shunting_yard.token;

import com.avispa.precedence_parsers.shunting_yard.OperatorAssociativity;
import lombok.AccessLevel;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.function.UnaryOperator;

/**
 * Unary operators
 * @author Rafał Hiszpański
 */
@Getter
public enum UnaryOperatorToken implements MathOperator {
    PLUS("+", 3, OperatorAssociativity.RIGHT, a -> a),
    MINUS("-", 3, OperatorAssociativity.RIGHT, a -> Operand.from(a.get().multiply(new BigDecimal("-1"))));

    private final String value;
    private final int precedence;
    private final OperatorAssociativity associativity;

    @Getter(AccessLevel.NONE)
    private final UnaryOperator<Operand> operation;

    UnaryOperatorToken(final String value, final int precedence, final OperatorAssociativity associativity, UnaryOperator<Operand> operation) {
        this.value = value;
        this.precedence = precedence;
        this.associativity = associativity;
        this.operation = operation;
    }

    public boolean isLeftAssociative() {
        return OperatorAssociativity.LEFT == this.associativity;
    }

    public boolean isRightAssociative() {
        return OperatorAssociativity.RIGHT == this.associativity;
    }

    public Operand apply(Operand a) {
        return operation.apply(a);
    }
}
