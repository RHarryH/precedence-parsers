package com.avispa.parser.precedence.output;

import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.output.AbstractOutputTransformer;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.parser.PrecedenceParser;
import com.avispa.parser.precedence.parser.SyntaxException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Prints full derivation for output from parser
 * @author Rafał Hiszpański
 */
public final class Derivation extends AbstractOutputTransformer<List<List<Symbol>>, Production> {

    public Derivation(PrecedenceParser<Production> parser) {
        super(parser);
    }

    @Override
    public List<List<Symbol>> parse(String input) throws SyntaxException, LexerException {
        List<Production> output = getParser().parse(input);

        if(output.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<Symbol>> derivation = new ArrayList<>(output.size());
        List<Symbol> sententialForm = null;

        var it = output.listIterator(output.size());
        while(it.hasPrevious()) {
            Production production = it.previous();
            if(null != sententialForm) {
                sententialForm = buildNextSententialForm(sententialForm, production);
            } else {
                sententialForm = addFirstSententialForm(derivation, production);
            }
            derivation.add(sententialForm);
        }

        return derivation;
    }

    /**
     * Locates lhs of the production in the current sentential form and applies the production by replacing lhs with
     * rhs symbols creating new sentential form
     * @param sententialForm
     * @param production
     * @return
     */
    private List<Symbol> buildNextSententialForm(List<Symbol> sententialForm, Production production) {
        int index = sententialForm.indexOf(production.getLhs()); // find first index

        List<Symbol> nextSententialForm = new ArrayList<>(sententialForm); // copy previous form
        nextSententialForm.remove(index);
        nextSententialForm.addAll(index, production.getRhs());
        return nextSententialForm;
    }

    /**
     * Add lhs and rhs of first production as first sentential forms of derivation
     * @param result
     * @param production
     * @return
     */
    private List<Symbol> addFirstSententialForm(List<List<Symbol>> result, Production production) {
        result.add(List.of(production.getLhs())); // start symbol

        return new ArrayList<>(production.getRhs());
    }
}
