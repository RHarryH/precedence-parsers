package com.avispa.precedence_parsers.shunting_yard.token;

import lombok.Getter;

@Getter
public enum Function implements Token {
	MODULO("mod", 2),
	SQRT("sqrt", 1),
	MAX("max", 2),
	MIN("min", 2);
	
	private final String symbol;
	private final int maxArgs;
	
	Function(final String symbol, final int maxArgs) {
		this.symbol = symbol;
		this.maxArgs = maxArgs;
	}
}
