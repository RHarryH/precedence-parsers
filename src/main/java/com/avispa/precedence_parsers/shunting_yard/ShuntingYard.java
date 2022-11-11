package com.avispa.precedence_parsers.shunting_yard;

import com.avispa.precedence_parsers.shunting_yard.token.FunctionToken;
import com.avispa.precedence_parsers.shunting_yard.token.MathOperator;
import com.avispa.precedence_parsers.shunting_yard.token.Misc;
import com.avispa.precedence_parsers.shunting_yard.token.Operand;
import com.avispa.precedence_parsers.shunting_yard.token.Token;
import com.avispa.precedence_parsers.shunting_yard.tokenizer.Tokenizer;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Slf4j
public class ShuntingYard implements IParser{

	/**
	 * Runs Shunting-yard algorithm for expressions parsing
	 * @param expression input string expression
	 * @return list of parsed tokens
	 */
	@Override
    public List<Token> parse(String expression) {
		List<Token> output = new ArrayList<>();
		Deque<Token> opStack = new ArrayDeque<>();
		Deque<Call> callStack = new ArrayDeque<>();

		Tokenizer tokenizer = new Tokenizer();
		List<Token> tokens = tokenizer.tokenize(expression);
		if(log.isDebugEnabled()) {
			log.debug("Expression \"{}\" has been tokenized to: {}", expression, tokens);
		}

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
				throw new IllegalStateException("Mismatched parentheses!");
			}
			output.add(token);
		}

		if(log.isDebugEnabled()) {
			log.debug("Output: {}", output);
		}
        
        return output;
    }

	/**
	 * Pops back to the place where the parenthesis was found. Left parenthesis is not popped from
	 * the stack.
	 * @param opStack operators stack
	 * @param output output token list
	 */
	private void closeParenthesesGroup(Deque<Token> opStack, List<Token> output) {
		while(!Misc.LEFT_PARENTHESIS.equals(opStack.peek())) {
			output.add(opStack.pop());
			if(opStack.isEmpty()) {
				throw new IllegalStateException("Missing left parenthesis");
			}
		}
	}

	/**
	 * When the call stack is not empty, increment argument list for the last element on the call stack.
	 * @param callStack stack containing information about functions
	 */
	private void incrementPeekFunctionArgumentCount(Deque<Call> callStack) {
		if(!callStack.isEmpty()) {
			callStack.peek().incArgumentCount();
		} else {
			throw new IllegalStateException("No function found. Comma used in the wrong place.");
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
	private void processFunctionClosure(Deque<Token> opStack, Deque<Call> callStack, List<Token> output) {
		if(opStack.peek() instanceof FunctionToken) {
			Call call = callStack.pop();

			call.incArgumentCount(); // right parenthesis closed last argument
			if(call.hasAllArguments()) {
				output.add(opStack.pop());
			} else {
				String message = String.format("Function does not have all arguments defined. Expected: %s, is: %s", call.getFunctionToken().getExpectedArgCount(), call.getArgCount());
				throw new IllegalStateException(message);
			}
		}
	}
}
