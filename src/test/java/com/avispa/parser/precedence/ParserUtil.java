package com.avispa.parser.precedence;

import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.Grammar;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Terminal;
import com.avispa.parser.precedence.parser.ParserCreationException;
import com.avispa.parser.precedence.parser.ParserFactory;
import com.avispa.parser.precedence.parser.PrecedenceParser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

import static com.avispa.parser.precedence.TestSymbols.A;
import static com.avispa.parser.precedence.TestSymbols.B;
import static com.avispa.parser.precedence.TestSymbols.C;
import static com.avispa.parser.precedence.TestSymbols.a;
import static com.avispa.parser.precedence.TestSymbols.b;

/**
 * @author Rafał Hiszpański
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParserUtil {
    public static PrecedenceParser<Production> getSampleParser() {
        Set<Terminal> terminals = Set.of(a, b, Terminal.BOUNDARY_MARKER);

        List<Production> productions = List.of(
                Production.of(A, List.of(Terminal.BOUNDARY_MARKER, B, Terminal.BOUNDARY_MARKER)),
                Production.of(B, List.of(B, a, C)),
                Production.of(B, List.of(a)),
                Production.of(C, List.of(b)));

        try {
            Grammar grammar = ContextFreeGrammar.from("Test", terminals, productions, A);
            return ParserFactory.newSimplePrecedenceParser(grammar);
        } catch (IncorrectGrammarException | ParserCreationException e) {
            throw new IllegalStateException("Parser should be initialized", e);
        }
    }
}
