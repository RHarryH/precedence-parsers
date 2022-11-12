package com.avispa.parser.shuntingyard.token;

import lombok.Getter;

/**
 * Here are the tokens, which are detected during lexical analysis, but later they are ignored by the
 * algorithm and should not show in the parsed results.
 */
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
