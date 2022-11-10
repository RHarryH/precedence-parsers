package com.avispa.precedence_parsers.shunting_yard;

import com.avispa.precedence_parsers.shunting_yard.token.Misc;
import com.avispa.precedence_parsers.shunting_yard.token.Operand;
import com.avispa.precedence_parsers.shunting_yard.token.Operator;
import com.avispa.precedence_parsers.shunting_yard.token.Token;
import com.avispa.precedence_parsers.shunting_yard.tokenizer.Tokenizer;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Slf4j
public class ShuntingYard {
    //private int argsCount = 1;
    
    public List<Token> parse(String expression) {
		List<Token> output = new ArrayList<>();
		Deque<Token> stack = new ArrayDeque<>();

		Tokenizer tokenizer = new Tokenizer();
		List<Token> tokens = tokenizer.tokenize(expression);
		if(log.isDebugEnabled()) {
			log.debug("Expression \"{}\" has been tokenized to: {}", expression, tokens);
		}

		for(Token token : tokens) {
			if(token instanceof Operand) {
				output.add(token);
			} else if(token instanceof Operator) {
				processOperator(output, stack, (Operator) token);
			} else if(Misc.LEFT_PARENTHESIS.equals(token)) {
				stack.push(token);
			} else if(Misc.RIGHT_PARENTHESIS.equals(token)) {
				while(!Misc.LEFT_PARENTHESIS.equals(stack.peek())) {
					output.add(stack.pop());
					if(stack.isEmpty()) {
						throw new IllegalStateException("Missing left parenthesis");
					}
				}
				stack.pop();
			}
		}

		while(!stack.isEmpty()) {
			Token token = stack.pop();
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

	private void processOperator(List<Token> output, Deque<Token> stack, Operator operator) {
		while(stack.peek() instanceof Operator) {
			Operator topOperator = (Operator) stack.peek();

			if(topOperator.getPrecedence() > operator.getPrecedence() ||
					(operator.isLeftAssociative() && topOperator.getPrecedence() == operator.getPrecedence())) {
				output.add(stack.pop());
			} else {
				break;
			}
		}
		stack.push(operator);
	}
    
    private void lookForTokens(StringBuilder sb, Deque<Token> stack, List<Object> output) throws ParseException {
        /*if(!stack.isEmpty() && stack.peek() instanceof Function) {
        	if(!sb.toString().startsWith(Misc.LEFT_PARENTHESIS.getSymbol()))
        		throw new ParseException("Parenthesis is expected after function name", 0);
        }*/

        if(/*!processFunctions(sb, stack) &&*/ /*!processOperators(sb, stack, output)*/true) {
        	int symbolLength = 1;
        	
        	if(sb.toString().startsWith(Misc.COMMA.getValue())) {
        		while(stack.peek() != Misc.LEFT_PARENTHESIS) {
        			output.add(stack.pop());
        			if(stack.isEmpty()) {
						throw new ParseException("Missing left parenthesis", 0);
					}
	        	}
        		//argsCount++;
        	} else if(sb.toString().startsWith(Misc.LEFT_PARENTHESIS.getValue()))
	        	stack.push(Misc.LEFT_PARENTHESIS);
        	else if(sb.toString().startsWith(Misc.RIGHT_PARENTHESIS.getValue())) {
	        	while(stack.peek() != Misc.LEFT_PARENTHESIS) {
	                output.add(stack.pop());
	                if(stack.isEmpty()) {
						throw new ParseException("Missing left parenthesis", 0);
					}
	        	}
	            stack.pop(); // pop left parenthesis
	            
	            /*if(!stack.isEmpty() && stack.peek() instanceof Function) {
	            	Function function = (Function)stack.pop();
	            	if(function.getArgsNum() != argsCount) {
						throw new ParseException("Incorrect arguments count for function " + function.getSymbol() + ". Expected count: " + function.getArgsNum(), 0);
					}
	            	argsCount = 1;
	            	output.add(function);
	            }*/
	        } else {
	        	StringBuilder variable = new StringBuilder();
	        	
	            for(int i = 0; i < sb.length(); ++i) {
	            	char c = sb.charAt(i);
	            	if(Character.isLetterOrDigit(c) || c == '.') // for floating point numbers
	            		variable.append(c);
	            	else 
	            		break;
	                    
	            }
	            output.add(variable.toString());
	            symbolLength = variable.length();
	        }
        	
        	sb = sb.delete(0, symbolLength);
        }
    }
    
    /*private boolean processFunctions(StringBuilder sb, Deque<Token> stack) {
    	// functions
    	for(Function function : Function.values()) {
    		if(sb.toString().startsWith(function.getSymbol())) {
	        	stack.push(function); 
	        	sb = sb.delete(0, function.getSymbol().length());
    			return true;
    		}
    	}
    
    	return false;
    }*/
}
