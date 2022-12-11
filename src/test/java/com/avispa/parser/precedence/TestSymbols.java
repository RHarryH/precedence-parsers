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
