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

package com.avispa.parser.misc.cli;

import com.avispa.parser.misc.tree.TreeNode;
import com.avispa.parser.misc.tree.TreePrinter;
import com.avispa.parser.shuntingyard.token.BinaryOperatorToken;
import com.avispa.parser.shuntingyard.token.Operand;
import com.avispa.parser.token.Token;
import org.apache.commons.cli.MissingArgumentException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rafał Hiszpański
 */
class CliServiceTest {
    private static final CliService cliService = new CliService();
    private final Operand two = Operand.from("2");

    @Test
    void givenNoInput_whenProcess_thenThrowException() {
        assertThrows(MissingArgumentException.class, () -> cliService.process(new String[]{""}));
    }

    @Test
    void givenInput_whenMissingInputArgument_thenThrowException() throws MissingArgumentException {
        assertEquals("", cliService.process(new String[]{"-input"}));
    }

    @Test
    void givenInput_whenDefaultOutput_thenReturnListOfTokens() throws MissingArgumentException {
        assertEquals(List.of(two, two, BinaryOperatorToken.ADD).toString(), cliService.process(new String[]{"-input", "2+2"}));
    }

    @Test
    void givenInput_whenMissingOutputArgument_thenReturnListOfTokens() throws MissingArgumentException {
        assertEquals("", cliService.process(new String[]{"-input", "2+2", "-output"}));
    }

    @Test
    void givenInput_whenTokenListOutput_thenReturnListOfTokens() throws MissingArgumentException {
        assertEquals(List.of(two, two, BinaryOperatorToken.ADD).toString(), cliService.process(new String[]{"-input", "2+2", "-output", OutputMode.TOKEN_LIST.getName()}));
    }

    @Test
    void givenInput_whenExpressionTreeOutput_thenReturnExpressionTree() throws MissingArgumentException {
        TreeNode<Token> tree = new TreeNode<>(BinaryOperatorToken.ADD);
        tree.addChild(new TreeNode<>(two));
        tree.addChild(new TreeNode<>(two));

        assertEquals(TreePrinter.print(tree), cliService.process(new String[]{"-input", "2+2", "-output", OutputMode.EXPRESSION_TREE.getName()}));
    }

    @Test
    void givenInput_whenRPNOutput_thenReturnRPN() throws MissingArgumentException {
        assertEquals("2 2 +", cliService.process(new String[]{"-input", "2+2", "-output", OutputMode.REVERSE_POLISH_NOTATION.getName()}));
    }

    @Test
    void givenInput_whenEvaluatedOutput_thenReturnResult() throws MissingArgumentException {
        assertEquals("4", cliService.process(new String[]{"-input", "2+2", "-output", OutputMode.EVALUATED.getName()}));
    }

    @Test
    void givenInput_whenUnknownOutput_thenReturnNothing() throws MissingArgumentException {
        assertEquals("", cliService.process(new String[]{"-input", "2+2", "-output", "unknown-output"}));
    }

    @Test
    void givenGrammarAndInput_whenMissingStartSymbol_thenReturnNothing() throws MissingArgumentException {
        assertEquals("", cliService.process(new String[]{"-input", "2+2", "-grammar", "src/test/resources/grammar/simple-precedence-grammar.txt"}));
    }

    @Test
    void givenGrammarAndInput_whenDefaultOutput_thenReturnListOfProductions() throws MissingArgumentException {
        assertEquals("[factor -> [NUMBER_1:2], term -> [factor], term_prime -> [term], expression -> [term_prime], factor -> [NUMBER_2:2], term -> [factor], term_prime -> [term], expression -> [expression, ADD_1:+, term_prime], expression_prime -> [expression]]",
                cliService.process(new String[]{"-input", "2+2", "-grammar", "src/test/resources/grammar/simple-precedence-grammar.txt", "-start-symbol", "expression_prime"}));
    }

    @Test
    void givenGrammarAndInput_whenParseTreeOutput_thenReturnParseTree() throws MissingArgumentException {
        String newLine = System.lineSeparator();
        String expectedParseTreeOutput =
                "expression_prime" + newLine +
                "└── expression" + newLine +
                "    ├── term_prime" + newLine +
                "    │   └── term" + newLine +
                "    │       └── factor" + newLine +
                "    │           └── NUMBER_2:2" + newLine +
                "    ├── ADD_1:+" + newLine +
                "    └── expression" + newLine +
                "        └── term_prime" + newLine +
                "            └── term" + newLine +
                "                └── factor" + newLine +
                "                    └── NUMBER_1:2" + newLine;

        assertEquals(expectedParseTreeOutput,
                cliService.process(new String[]{"-input", "2+2", "-output", OutputMode.PARSE_TREE.getName(), "-grammar", "src/test/resources/grammar/simple-precedence-grammar.txt", "-start-symbol", "expression_prime"}));
    }

    @Test
    void givenGrammarAndInput_whenDerivationOutput_thenReturnDerivation() throws MissingArgumentException {
        assertEquals("[[expression_prime], [expression], [expression, ADD_1:+, term_prime], [expression, ADD_1:+, term], [expression, ADD_1:+, factor], [expression, ADD_1:+, NUMBER_2:2], [term_prime, ADD_1:+, NUMBER_2:2], [term, ADD_1:+, NUMBER_2:2], [factor, ADD_1:+, NUMBER_2:2], [NUMBER_1:2, ADD_1:+, NUMBER_2:2]]",
                cliService.process(new String[]{"-input", "2+2", "-output", OutputMode.DERIVATION.getName(), "-grammar", "src/test/resources/grammar/simple-precedence-grammar.txt", "-start-symbol", "expression_prime"}));
    }

    @Test
    void givenNonExistingGrammarFile_whenProcess_thenReturnNothing() throws MissingArgumentException {
        assertEquals("",
                cliService.process(new String[]{"-input", "2+2", "-grammar", "non-existing-grammar-file.txt", "-start-symbol", "expression_prime"}));
    }

    @Test
    void givenGrammar_whenUnknownSymbol_thenReturnNothing() throws MissingArgumentException {
        assertEquals("", cliService.process(new String[]{"-input", "a", "-output", OutputMode.DERIVATION.getName(), "-grammar", "src/test/resources/grammar/simple-precedence-grammar.txt", "-start-symbol", "expression_prime"}));
    }

    @Test
    void givenGrammar_whenSyntaxError_thenReturnNothing() throws MissingArgumentException {
        assertEquals("", cliService.process(new String[]{"-input", "12", "-output", OutputMode.DERIVATION.getName(), "-grammar", "src/test/resources/grammar/simple-precedence-grammar.txt", "-start-symbol", "expression_prime"}));
    }
}