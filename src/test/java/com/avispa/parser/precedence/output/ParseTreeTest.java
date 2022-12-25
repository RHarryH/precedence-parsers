package com.avispa.parser.precedence.output;

import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.misc.tree.TreePrinter;
import com.avispa.parser.precedence.grammar.ContextFreeGrammar;
import com.avispa.parser.precedence.grammar.GrammarFile;
import com.avispa.parser.precedence.grammar.IncorrectGrammarException;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.parser.ParserFactory;
import com.avispa.parser.precedence.parser.PrecedenceParser;
import com.avispa.parser.precedence.parser.SyntaxException;
import org.junit.jupiter.api.Test;

import static com.avispa.parser.precedence.TestSymbols.expression;

/**
 * @author Rafał Hiszpański
 */
class ParseTreeTest {

    @Test
    void test() throws LexerException, SyntaxException, IncorrectGrammarException {
        ContextFreeGrammar grammar = ContextFreeGrammar.fromWithBoundaryMarker(new GrammarFile("src/test/resources/grammar/simple-precedence-grammar.txt"), expression);
        PrecedenceParser<Production> parser = ParserFactory.newSimplePrecedenceParser(grammar);
        //List<Production> productionsOrder = parser.parse();

        //System.out.println(productionsOrder);


        System.out.println(TreePrinter.print(new ParseTree(parser).parse("1+2+3*4")));
    }
}