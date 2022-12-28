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

package com.avispa.parser.shuntingyard.output;

import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.output.AbstractOutputTransformer;
import com.avispa.parser.precedence.parser.SyntaxException;
import com.avispa.parser.shuntingyard.ShuntingYard;
import com.avispa.parser.token.Token;

import java.util.stream.Collectors;

/**
 * @author Rafał Hiszpański
 */
public final class ReversePolishNotationText extends AbstractOutputTransformer<String, Token> {
    public ReversePolishNotationText() {
        super(new ShuntingYard());
    }

    @Override
    public String parse(String expression) throws SyntaxException, LexerException {
        return getParser()
                .parse(expression)
                .stream()
                .map(Token::getValue)
                .collect(Collectors.joining(" "));
    }
}
