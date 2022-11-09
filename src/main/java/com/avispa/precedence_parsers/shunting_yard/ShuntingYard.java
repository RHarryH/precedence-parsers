package com.avispa.precedence_parsers.shunting_yard;

import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;

import com.avispa.precedence_parsers.shunting_yard.token.Function;
import com.avispa.precedence_parsers.shunting_yard.token.Misc;
import com.avispa.precedence_parsers.shunting_yard.token.Operator;
import com.avispa.precedence_parsers.shunting_yard.token.Token;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShuntingYard {
    private int argsCount = 1;
    
    public String parse(String expression) {
		List<Object> output = new ArrayList<>();
		Deque<Token> stack = new ArrayDeque<>();

		//expression = unaryPreprocess(expression);

        StringBuilder parsingExpression = new StringBuilder(expression);
        while(parsingExpression.length() > 0) {
        	try {
        		lookForTokens(parsingExpression, stack, output);
        	} catch(ParseException e) {
        		log.error(e.toString());
        		return "";
        	}
        }
        
        while(!stack.isEmpty()) {
			output.add(stack.pop());
		}
        
        return output.toString();
    }

	/*private String unaryPreprocess(String expression) {
		expression = expression.replace(" ", "").replace("(-", "(0-")
				.replace(",-", ",0-");
		if(expression.startsWith("-(")) {
			expression = "-1*(" + expression.substring(2);
		}
		if(expression.charAt(0) == '-') {
			expression = "0" + expression;
		}
		return expression;
	}*/
    
    private void lookForTokens(StringBuilder sb, Deque<Token> stack, List<Object> output) throws ParseException {
        /*unaryMinus(stack, output);
        
        if(!stack.isEmpty() && stack.peek() instanceof Function) {
        	if(!sb.toString().startsWith(Misc.LEFT_PARENTHESIS.getSymbol()))
        		throw new ParseException("Parenthesis is expected after function name", 0);
        }*/

        if(/*!processFunctions(sb, stack) &&*/ !processOperators(sb, stack, output)) {
        	int symbolLength = 1;
        	
        	if(sb.toString().startsWith(Misc.COMMA.getSymbol())) {
        		while(stack.peek() != Misc.LEFT_PARENTHESIS) {
        			output.add(stack.pop());
        			if(stack.isEmpty()) {
						throw new ParseException("Missing left parenthesis", 0);
					}
	        	}
        		argsCount++;
        	} else if(sb.toString().startsWith(Misc.LEFT_PARENTHESIS.getSymbol()))
	        	stack.push(Misc.LEFT_PARENTHESIS);
        	else if(sb.toString().startsWith(Misc.RIGHT_PARENTHESIS.getSymbol())) {
	        	while(stack.peek() != Misc.LEFT_PARENTHESIS) {
	                output.add(stack.pop());
	                if(stack.isEmpty()) {
						throw new ParseException("Missing left parenthesis", 0);
					}
	        	}
	            stack.pop(); // pop left parenthesis
	            
	            if(!stack.isEmpty() && stack.peek() instanceof Function) {
	            	Function function = (Function)stack.pop();
	            	if(function.getMaxArgs() != argsCount) {
						throw new ParseException("Incorrect arguments count for function " + function.getSymbol() + ". Expected count: " + function.getMaxArgs(), 0);
					}
	            	argsCount = 1;
	            	output.add(function);
	            }
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

	/*private void unaryMinus(Deque<Token> stack, List<Object> output) {
		if(output.size() >= 2) {
    		if(!stack.isEmpty() && stack.peek().equals(Operator.SUBTRACT) && output.get(output.size() - 2).equals("0") ) {
    			stack.pop();
    			output.remove(output.size() - 2);
    			output.set(output.size() - 1, "-" + output.get(output.size() - 1));
    		}
    	}
	}*/
    
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

	private boolean processOperators(StringBuilder sb, Deque<Token> stack, List<Object> output) {
		for(Operator operator : Operator.values()) {
        	if(sb.toString().startsWith(operator.getSymbol())) {
            	while(!stack.isEmpty() && stack.peek() instanceof Operator) {
        			Operator top = (Operator) stack.peek();

                    if((operator.isLeftAssociative() && operator.getPrecedence() <= top.getPrecedence()) ||
                       (operator.isRightAssociative() && operator.getPrecedence() < top.getPrecedence())) {
						output.add(stack.pop());
					} else {
						break;
					}
                }
            	stack.push(operator); 
            	sb = sb.delete(0, operator.getSymbol().length());
            	return true;
        	}
        }
		return false;
	}

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        log.info("Put expression: ");
        
        Scanner scanner = new Scanner(System.in);
        
        String expression = scanner.nextLine();
        
        scanner.close();
        
        ShuntingYard shuntingYard = new ShuntingYard();
		log.info(shuntingYard.parse(expression));
    }
}
