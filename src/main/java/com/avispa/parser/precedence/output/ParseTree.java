package com.avispa.parser.precedence.output;

import com.avispa.parser.lexer.LexerException;
import com.avispa.parser.misc.tree.TreeNode;
import com.avispa.parser.output.AbstractOutputTransformer;
import com.avispa.parser.precedence.grammar.NonTerminal;
import com.avispa.parser.precedence.grammar.Production;
import com.avispa.parser.precedence.grammar.Symbol;
import com.avispa.parser.precedence.parser.SimplePrecedenceParser;
import com.avispa.parser.precedence.parser.SyntaxException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * @author Rafał Hiszpański
 */
public final class ParseTree extends AbstractOutputTransformer<TreeNode<Symbol>, Production> {

    public ParseTree(SimplePrecedenceParser parser) {
        super(parser);
    }

    @Override
    public TreeNode<Symbol> parse(String input) throws SyntaxException, LexerException {
        Deque<Production> productions = new ArrayDeque<>(getParser().parse(input));

        if(productions.isEmpty()) {
            return null;
        }

        TreeNode<Symbol> root = new TreeNode<>(productions.peekLast().getLhs());
        buildParseTree(root, productions);

        return root;
    }

    private void buildParseTree(TreeNode<Symbol> currentNode, Deque<Production> productions) {
        Production currentProduction = productions.removeLast(); // get last production on the stack, this is the first derivation from start symbol
        List<Symbol> currentRhs = currentProduction.getRhs();
        var it = currentRhs.listIterator(currentRhs.size());

        // iterate productions backwards, this is because how parser produces the list of productions
        while(it.hasPrevious()) {
            Symbol symbol = it.previous();
            var node = new TreeNode<>(symbol); // create new node

            if(symbol instanceof NonTerminal) { // for non-terminals we want to expand production
                Production production = productions.peekLast();
                if (production != null && symbol.equals(production.getLhs())) { // if production
                    buildParseTree(node, productions);
                }
            }

            currentNode.addChild(node);
        }
    }
}
