package com.avispa.parser.shuntingyard.token;

/**
 * @author Rafał Hiszpański
 */
public interface MathOperator extends Token {
	int getPrecedence();

	boolean isLeftAssociative();
	boolean isRightAssociative();
}
