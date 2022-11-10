package com.avispa.precedence_parsers.shunting_yard.token;

import lombok.Getter;

/**
 * @author Rafał Hiszpański
 */
@Getter
public enum Function implements Token {
	MODULO("mod", 2),
	SQRT("sqrt", 1),
	MAX("max", 2),
	MIN("min", 2);
	
	private final String value;
	private final int expectedArgCount;
	
	Function(final String value, final int expectedArgCount) {
		this.value = value;
		this.expectedArgCount = expectedArgCount;
	}
}
