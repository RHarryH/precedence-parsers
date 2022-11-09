package com.avispa.precedence_parsers.shunting_yard;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import com.avispa.precedence_parsers.shunting_yard.token.Function;
import com.avispa.precedence_parsers.shunting_yard.token.Misc;
import com.avispa.precedence_parsers.shunting_yard.token.Operator;
import com.avispa.precedence_parsers.shunting_yard.token.Token;

public class ReversePolishNotation {
    
    private String expression;
    private List<Object> rpn = new ArrayList<>();
    private Stack<Token> stack = new Stack<>();
    
    private Integer argsCount = 1;

    public ReversePolishNotation(String expression) {
        expression = expression.replace(" ", "").replace("(-", "(0-")
                .replace(",-", ",0-");
        if(expression.startsWith("-("))
        	expression = "-1*(" + expression.substring(2);
        if(expression.charAt(0) == '-')
            expression = "0" + expression;
        
        this.expression = expression;

    }
    
    public String getRPN() {
        return rpn.toString();
    }
    
    public Boolean parse() {
        StringBuilder parsingExpression = new StringBuilder(expression);
        rpn.clear();
        while(parsingExpression.length() > 0) {
        	try {
        		lookForTokens(parsingExpression);
        	} catch(ParseException e) {
        		System.err.println(e.toString());
        		rpn.clear();
        		return false;
        	}
        }
        
        while(!stack.isEmpty()) 
        	rpn.add(stack.pop());
        
        return true;
    }
    
    private void lookForTokens(StringBuilder sb) throws ParseException {

        unaryMinus();
        
        if(!stack.isEmpty() && stack.peek() instanceof Function) {
        	if(!sb.toString().startsWith(Misc.LEFT_PARENTHESIS.getSymbol()))
        		throw new ParseException("Parenthesis is expected after function name", 0);
        }

        if(!processFunctions(sb) && !processOperators(sb)) {
        	Integer symbolLength = 1;
        	
        	if(sb.toString().startsWith(Misc.COMMA.getSymbol())) {
        		while(stack.peek() != Misc.LEFT_PARENTHESIS) {
        			rpn.add(stack.pop());
        			if(stack.empty())
        				throw new ParseException("Missing left parenthesis", 0);
	        	}
        		argsCount++;
        	} else if(sb.toString().startsWith(Misc.LEFT_PARENTHESIS.getSymbol()))
	        	stack.push(Misc.LEFT_PARENTHESIS);
        	else if(sb.toString().startsWith(Misc.RIGHT_PARENTHESIS.getSymbol())) {
	        	while(stack.peek() != Misc.LEFT_PARENTHESIS) {
	                rpn.add(stack.pop());
	                if(stack.empty())
	                	throw new ParseException("Missing left parenthesis", 0);
	        	}
	            stack.pop(); // pop left parenthesis
	            
	            if(!stack.isEmpty() && stack.peek() instanceof Function) {
	            	Function function = (Function)stack.pop();
	            	if(!function.getMaxArgs().equals(argsCount))
	            		throw new ParseException("Incorrect arguments count for function " + function.getSymbol() + ". Expected count: " + function.getMaxArgs(), 0);
	            	argsCount = 1;
	            	rpn.add(function);
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
	            rpn.add(variable.toString());
	            symbolLength = variable.length();
	        }
        	
        	sb = sb.delete(0, symbolLength);
        }
    }

	/**
	 * 
	 */
	private void unaryMinus() {
		if(rpn.size() >= 2) {
    		if(!stack.isEmpty() && stack.peek().equals(Operator.SUBTRACT) && rpn.get(rpn.size() - 2).equals("0") ) {
    			stack.pop();
    			rpn.remove(rpn.size() - 2);
    			rpn.set(rpn.size() - 1, "-" + (String) rpn.get(rpn.size() - 1));
    		}
    	}
	}
    
    private Boolean processFunctions(StringBuilder sb) {
    	// functions
    	for(Function function : Function.values()) {
    		if(sb.toString().startsWith(function.getSymbol())) {
	        	stack.push(function); 
	        	sb = sb.delete(0, function.getSymbol().length());
    			return true;
    		}
    	}
    
    	return false;
    }

	/**
	 * @param sb
	 * @param symbolLength
	 * @return
	 */
	private Boolean processOperators(StringBuilder sb) {
		for(Operator operator : Operator.values()) {
        	if(sb.toString().startsWith(operator.getSymbol())) {
            	while(!stack.isEmpty() && stack.peek() instanceof Operator) {
        			Operator top = (Operator) stack.peek();

                    if((operator.getSyntaxAssociativity().equals(SyntaxAssociativity.LEFT) && operator.getPriority() <= top.getPriority()) ||
                       (operator.getSyntaxAssociativity().equals(SyntaxAssociativity.RIGHT) && operator.getPriority() < top.getPriority()))
                    	rpn.add(stack.pop());
                    else 
                    	break;
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
        System.out.println("Put expression: ");
        
        Scanner scanner = new Scanner(System.in);
        
        String expression = scanner.nextLine();
        
        scanner.close();
        
        ReversePolishNotation reversePolishNotation = new ReversePolishNotation(expression);
        
        if(reversePolishNotation.parse())
        	System.out.println(reversePolishNotation.getRPN());
    }
}
