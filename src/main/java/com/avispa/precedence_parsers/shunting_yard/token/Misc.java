package com.avispa.precedence_parsers.shunting_yard.token;

import lombok.Getter;

@Getter
public enum Misc implements Token {
	COMMA(","),
	PERCENT("%"),
	LEFT_PARENTHESIS("("),
	RIGHT_PARENTHESIS(")");

	private final String symbol;
	
	Misc(final String symbol) {
		this.symbol = symbol;
	}
}
