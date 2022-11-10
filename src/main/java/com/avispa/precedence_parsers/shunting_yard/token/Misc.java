package com.avispa.precedence_parsers.shunting_yard.token;

import lombok.Getter;

@Getter
public enum Misc implements Token {
	COMMA(","),
	LEFT_PARENTHESIS("("),
	RIGHT_PARENTHESIS(")");

	private final String value;
	
	Misc(final String value) {
		this.value = value;
	}
}
