package com.avispa.parser.shuntingyard.token;

import lombok.AccessLevel;
import lombok.Getter;

import java.math.MathContext;
import java.util.function.Function;

/**
 * @author Rafał Hiszpański
 */
@Getter
public enum FunctionToken implements Token {
	MODULO("mod", 2, args -> Operand.from(args[0].get().remainder(args[1].get()))),
	SQRT("sqrt", 1, args -> Operand.from(args[0].get().sqrt(MathContext.DECIMAL32))),
	MAX("max", 2, args -> Operand.from(args[0].get().max(args[1].get()))),
	MIN("min", 2, args -> Operand.from(args[0].get().min(args[1].get())));
	
	private final String value;
	private final int expectedArgCount;

	@Getter(AccessLevel.NONE)
	private final Function<Operand[], Operand> operation;

	FunctionToken(final String value, final int expectedArgCount, Function<Operand[], Operand> operation) {
		this.value = value;
		this.expectedArgCount = expectedArgCount;
		this.operation = operation;
	}

	public Operand apply(Operand[] arguments) {
		if(arguments.length != expectedArgCount) {
			String message = String.format("Expected %d arguments. Got %d.", expectedArgCount, arguments.length);
			throw new IllegalArgumentException(message);
		}

		return operation.apply(arguments);
	}
}
