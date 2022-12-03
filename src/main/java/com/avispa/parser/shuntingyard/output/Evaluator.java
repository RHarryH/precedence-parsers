package com.avispa.parser.shuntingyard.output;

import com.avispa.parser.shuntingyard.Parser;
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
public final class Evaluator extends AbstractOutputTransformer<BigDecimal> {
    public Evaluator() {
        super();
    }

    public Evaluator(Parser parser) {
        super(parser);
    }

    @Override
    public BigDecimal parse(String expression) {
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
