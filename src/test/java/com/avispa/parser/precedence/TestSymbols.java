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

package com.avispa.parser.precedence;

import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Terminal;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Rafał Hiszpański
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestSymbols {
    public static final NonTerminal start = NonTerminal.of("start");
    public static final NonTerminal expression = NonTerminal.of("expression");
    public static final NonTerminal expression_prime = NonTerminal.of("expression_prime");
    public static final NonTerminal term = NonTerminal.of("term");
    public static final NonTerminal term_prime = NonTerminal.of("term_prime");
    public static final NonTerminal factor = NonTerminal.of("factor");

    public static final Terminal add = Terminal.of("ADD", "\\+");
    public static final Terminal mul = Terminal.of("MUL", "\\*");
    public static final Terminal lpar = Terminal.of("LEFT_PARENTHESIS", "\\(");
    public static final Terminal rpar = Terminal.of("RIGHT_PARENTHESIS", "\\)");
    public static final Terminal marker = Terminal.BOUNDARY_MARKER;
    public static final Terminal number = Terminal.of("NUMBER", "[0-9]");

    public static final NonTerminal A = NonTerminal.of("A");
    public static final NonTerminal B = NonTerminal.of("B");
    public static final NonTerminal C = NonTerminal.of("C");
    public static final NonTerminal D = NonTerminal.of("D");

    public static final Terminal a = Terminal.of("a", "a");
    public static final Terminal b = Terminal.of("b", "b");
}
