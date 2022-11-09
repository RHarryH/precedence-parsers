package com.avispa.precedence_parsers.shunting_yard.token;

import calculator.SyntaxAssociativity;
import com.avispa.precedence_parsers.shunting_yard.SyntaxAssociativity;

public enum Operator implements Token {
	ADD("+", 1, SyntaxAssociativity.LEFT),
	SUBTRACT("-", 1, SyntaxAssociativity.LEFT),
	MULTIPLY("*", 2, SyntaxAssociativity.LEFT),
	DIVIDE("/", 2, SyntaxAssociativity.LEFT),
	POWER("^", 3, SyntaxAssociativity.RIGHT);
	
	private final String symbol;
	private final Integer priority;
	private final SyntaxAssociativity ass;
	
	Operator(final String symbol, final Integer priority, final SyntaxAssociativity ass) {
		this.symbol = symbol;
		this.priority = priority;
		this.ass = ass;
	}
	
	public String getSymbol() {
		return symbol;
	}

	public Integer getPriority() {
		return priority;
	}

	public SyntaxAssociativity getSyntaxAssociativity() {
		return ass;
	}
}
