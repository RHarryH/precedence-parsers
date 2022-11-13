package com.avispa.parser.misc.cli;

import com.avispa.parser.misc.tree.TreeNode;
import com.avispa.parser.misc.tree.TreePrinter;
import com.avispa.parser.shuntingyard.token.BinaryOperatorToken;
import com.avispa.parser.shuntingyard.token.Operand;
import com.avispa.parser.shuntingyard.token.Token;
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
    void givenInput_whenMissingInputArgument_thenThrowException() {
        assertThrows(MissingArgumentException.class, () -> cliService.process(new String[]{"-input"}));
    }

    @Test
    void givenInput_whenDefaultOutput_thenReturnListOfTokens() throws MissingArgumentException {
        assertEquals(List.of(two, two, BinaryOperatorToken.ADD).toString(), cliService.process(new String[]{"-input", "2+2"}));
    }

    @Test
    void givenInput_whenMissingOutputArgument_thenReturnListOfTokens() {
        assertThrows(MissingArgumentException.class, () -> cliService.process(new String[]{"-input", "2+2", "-output"}));
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
}