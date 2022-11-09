package com.avispa.precedence_parsers.shunting_yard.token;

public enum Function implements Token {
	MODULO("mod", 2),
	SQRT("sqrt", 1),
	MAX("max", 2),
	MIN("min", 2);
	
	private final String symbol;
	private final Integer maxArgs;
	
	Function(final String symbol, final Integer maxArgs) {
		this.symbol = symbol;
		this.maxArgs = maxArgs;
	}

	public String getSymbol() {
		return symbol;
	}

	public Integer getMaxArgs() {
		return maxArgs;
	}
}
