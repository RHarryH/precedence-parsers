package com.avispa.precedence_parsers.shunting_yard.token;

public interface Operator extends Token {
	int getPrecedence();

	boolean isLeftAssociative();
	boolean isRightAssociative();
}
