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

package com.avispa.parser.shuntingyard.output;

import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.output.AbstractOutputTransformer;
import com.avispa.parser.precedence.parser.SyntaxException;
import com.avispa.parser.shuntingyard.ShuntingYard;
import com.avispa.parser.shuntingyard.token.BinaryOperatorToken;
import com.avispa.parser.shuntingyard.token.FunctionToken;
import com.avispa.parser.shuntingyard.token.Operand;
import com.avispa.parser.shuntingyard.token.UnaryOperatorToken;
import com.avispa.parser.token.Token;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Rafał Hiszpański
 */
public final class Evaluator extends AbstractOutputTransformer<BigDecimal, Token> {
    public Evaluator() {
        super(new ShuntingYard());
    }

    @Override
    public BigDecimal parse(String expression) throws SyntaxException, LexerException {
        List<Token> output = getParser().parse(expression);

        if(output.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Deque<Operand> operandStack = new ArrayDeque<>();
        for(Token token : output) {
            if(token instanceof Operand) {
                operandStack.push((Operand) token);
            } else if(token instanceof UnaryOperatorToken) {
                UnaryOperatorToken unary = (UnaryOperatorToken)token;
                operandStack.push(unary.apply(operandStack.pop()));
            } else if(token instanceof BinaryOperatorToken) {
                BinaryOperatorToken binary = (BinaryOperatorToken)token;
                operandStack.push(binary.apply(operandStack.pop(), operandStack.pop()));
            } else if(token instanceof FunctionToken) {
                FunctionToken function = (FunctionToken)token;
                operandStack.push(function.apply(getFunctionArguments(operandStack, function)));
            } else {
                String message = String.format("Token %s can't be evaluated", token);
                throw new IllegalStateException(message);
            }
        }

        if(operandStack.size() == 1) {
            return operandStack.pop().get().stripTrailingZeros();
        } else {
            throw new IllegalStateException("Evaluation failed. There are more operands on the stack than expected!");
        }
    }

    private Operand[] getFunctionArguments(Deque<Operand> operandStack, FunctionToken function) {
        return IntStream.range(0, function.getExpectedArgCount())
                .mapToObj(i -> operandStack.pop())
                .toArray(Operand[]::new);
    }
}
