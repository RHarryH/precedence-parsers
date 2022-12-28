/*
 * MIT License
 *
 * Copyright (c) 2022 Rafał Hiszpański
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.avispa.parser.shuntingyard.token;

import com.avispa.parser.token.Token;
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
