package com.avispa.parser.shuntingyard.token;

public interface MathOperator extends Token {
	int getPrecedence();

	boolean isLeftAssociative();
	boolean isRightAssociative();
}
