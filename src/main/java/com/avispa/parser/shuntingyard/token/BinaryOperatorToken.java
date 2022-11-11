package com.avispa.parser.shuntingyard.token;

import com.avispa.parser.shuntingyard.OperatorAssociativity;
import lombok.AccessLevel;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BinaryOperator;

/**
 * Binary operators
 * @author Rafał Hiszpański
 */
@Getter
public enum BinaryOperatorToken implements MathOperator {
    ADD("+", 1, OperatorAssociativity.LEFT, (b, a) -> Operand.from(a.get().add(b.get()))),
    SUBTRACT("-", 1, OperatorAssociativity.LEFT, (b, a) -> Operand.from(a.get().subtract(b.get()))),
    MULTIPLY("*", 2, OperatorAssociativity.LEFT, (b, a) -> Operand.from(a.get().multiply(b.get()))),
    DIVIDE("/", 2, OperatorAssociativity.LEFT, (b, a) -> Operand.from(a.get().divide(b.get(), RoundingMode.HALF_UP))),
    POWER("^", 3, OperatorAssociativity.RIGHT, applyPower());

    private static BinaryOperator<Operand> applyPower() {
        return (b, a) -> {
            BigDecimal base = a.get();
            int exponent = getExponent(b);
            return Operand.from(base.pow(exponent));
        };
    }

    private static int getExponent(Operand b) {
        BigDecimal exponent = b.get();
        if(exponent.remainder(BigDecimal.ONE).equals(BigDecimal.ZERO)) {
            return Integer.parseInt(b.getValue());
        } else {
            throw new IllegalArgumentException("Exponent must be an integer number");
        }
    }

    private final String value;
    private final int precedence;
    private final OperatorAssociativity associativity;

    @Getter(AccessLevel.NONE)
    private final BinaryOperator<Operand> operation;

    BinaryOperatorToken(final String value, final int precedence, final OperatorAssociativity associativity, BinaryOperator<Operand> operation) {
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

    public Operand apply(Operand a, Operand b) {
        return operation.apply(a, b);
    }
}
