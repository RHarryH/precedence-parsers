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

package com.avispa.parser.shuntingyard;

import com.avispa.parser.Parser;
import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.precedence.parser.SyntaxException;
import com.avispa.parser.shuntingyard.token.FunctionToken;
import com.avispa.parser.shuntingyard.token.MathOperator;
import com.avispa.parser.shuntingyard.token.Misc;
import com.avispa.parser.shuntingyard.token.Operand;
import com.avispa.parser.shuntingyard.tokenizer.Tokenizer;
import com.avispa.parser.token.Token;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * @author Rafał Hiszpański
 */
@Slf4j
public class ShuntingYard implements Parser<Token> {

	/**
	 * Runs Shunting-yard algorithm for expressions parsing.
	 * @param expression input string expression
	 * @return list of parsed tokens
	 */
	@Override
    public List<Token> parse(String expression) throws LexerException, SyntaxException {
		List<Token> output = new ArrayList<>();
		Deque<Token> opStack = new ArrayDeque<>();
		Deque<Call> callStack = new ArrayDeque<>();

		Tokenizer tokenizer = new Tokenizer();
		List<Token> tokens = tokenizer.tokenize(expression);
		log.debug("Expression \"{}\" has been tokenized to: {}", expression, tokens);

		for(Token token : tokens) {
			if (token instanceof Operand) {
				output.add(token);
			} else if (token instanceof FunctionToken) {
				Call call = new Call((FunctionToken) token);
				callStack.push(call);
				opStack.push(token);
			} else if (Misc.COMMA.equals(token)) {
				closeParenthesesGroup(opStack, output);

				incrementPeekFunctionArgumentCount(callStack);
			} else if ( token instanceof MathOperator) {
				processOperator((MathOperator) token, opStack, output);
			} else if ( Misc.LEFT_PARENTHESIS.equals(token) ) {
				opStack.push(token);
			} else if ( Misc.RIGHT_PARENTHESIS.equals(token) ) {
				closeParenthesesGroup(opStack, output);
				opStack.pop(); // pop left parenthesis from the stack and ignore

				processFunctionClosure(opStack, callStack, output);
			}
		}

		while(!opStack.isEmpty()) {
			Token token = opStack.pop();
			if(Misc.LEFT_PARENTHESIS.equals(token)) {
				throw new SyntaxException("Mismatched parentheses!");
			}
			output.add(token);
		}

		log.debug("Output: {}", output);
        
        return output;
    }

	/**
	 * Pops back to the place where the parenthesis was found. Left parenthesis is not popped from
	 * the stack.
	 * @param opStack operators stack
	 * @param output output token list
	 */
	private void closeParenthesesGroup(Deque<Token> opStack, List<Token> output) throws SyntaxException {
		while(!Misc.LEFT_PARENTHESIS.equals(opStack.peek())) {
			output.add(opStack.pop());
			if(opStack.isEmpty()) {
				throw new SyntaxException("Missing left parenthesis");
			}
		}
	}

	/**
	 * When the call stack is not empty, increment argument list for the last element on the call stack.
	 * @param callStack stack containing information about functions
	 */
	private void incrementPeekFunctionArgumentCount(Deque<Call> callStack) throws SyntaxException {
		if(!callStack.isEmpty()) {
			callStack.peek().incArgumentCount();
		} else {
			throw new SyntaxException("No function found. Comma used in the wrong place.");
		}
	}

	/**
	 * Standard Shunting-yard algorithm for processing (math) operators.
	 * @param operator current operator
	 * @param opStack operators stack
	 * @param output output token list
	 */
	private void processOperator(MathOperator operator, Deque<Token> opStack, List<Token> output) {
		while(opStack.peek() instanceof MathOperator) {
			MathOperator topOperator = (MathOperator) opStack.peek();

			if(topOperator.getPrecedence() > operator.getPrecedence() ||
					(operator.isLeftAssociative() && topOperator.getPrecedence() == operator.getPrecedence())) {
				output.add(opStack.pop());
			} else {
				break;
			}
		}
		opStack.push(operator);
	}

	/**
	 * Invoked when right parenthesis was detected. Checks if the parenthesis was related to a function.
	 * If yes then simple validation is performed and function is eventually added to the result.
	 * @param opStack operators stack
	 * @param callStack stack containing information about functions
	 * @param output output token list
	 */
	private void processFunctionClosure(Deque<Token> opStack, Deque<Call> callStack, List<Token> output) throws SyntaxException {
		if(opStack.peek() instanceof FunctionToken) {
			Call call = callStack.pop();

			call.incArgumentCount(); // right parenthesis closed last argument
			if(call.hasAllArguments()) {
				output.add(opStack.pop());
			} else {
				String message = String.format("Function does not have all arguments defined. Expected: %s, is: %s", call.getFunctionToken().getExpectedArgCount(), call.getArgCount());
				throw new SyntaxException(message);
			}
		}
	}
}
