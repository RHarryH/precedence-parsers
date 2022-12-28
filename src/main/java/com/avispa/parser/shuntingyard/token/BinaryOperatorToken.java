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
