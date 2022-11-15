package com.avispa.parser.shuntingyard.token;

import com.avispa.parser.token.Token;

/**
 * @author Rafał Hiszpański
 */
public interface MathOperator extends Token {
	int getPrecedence();

	boolean isLeftAssociative();
	boolean isRightAssociative();
}
