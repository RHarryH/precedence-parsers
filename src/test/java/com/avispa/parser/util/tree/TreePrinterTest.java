package com.avispa.parser.util.tree;

import com.avispa.parser.shuntingyard.token.BinaryOperatorToken;
import com.avispa.parser.shuntingyard.token.FunctionToken;
import com.avispa.parser.shuntingyard.token.Operand;
import com.avispa.parser.shuntingyard.token.Token;
import com.avispa.parser.shuntingyard.token.UnaryOperatorToken;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rafał Hiszpański
 */
class TreePrinterTest {
    private static final String NEW_LINE = System.lineSeparator();
    @Test
    void givenNothing_whenPrint_thenEmptyString() {
        assertEquals("", TreePrinter.print(null));
    }

    @Test
    void givenRootOnly_whenPrint_thenRootValuePrinted() {
        Operand two = Operand.from("2");
        TreeNode<Token> root = new TreeNode<>(two);

        assertEquals("2" + NEW_LINE, TreePrinter.print(root));
    }

    @Test
    void givenSingleChild_whenPrint_thenLastChildPointerIsUsed() {
        TreeNode<Token> root = new TreeNode<>(BinaryOperatorToken.ADD);
        root.addChild(new TreeNode<>(FunctionToken.MAX));

        String expected =
                "ADD" + NEW_LINE +
                "└── MAX" + NEW_LINE;

        assertEquals(expected, TreePrinter.print(root));
    }

    @Test
    void givenChildren_whenPrint_thenCorrectPointersAreUsed() {
        TreeNode<Token> root = new TreeNode<>(BinaryOperatorToken.ADD);
        root.addChild(new TreeNode<>(FunctionToken.MAX));
        root.addChild(new TreeNode<>(UnaryOperatorToken.PLUS));

        String expected =
                "ADD" + NEW_LINE +
                "├── MAX" + NEW_LINE +
                "└── PLUS" + NEW_LINE;

        assertEquals(expected, TreePrinter.print(root));
    }

    @Test
    void givenNestedChildInLastNode_whenPrint_thenCorrectPointersAreUsed() {
        TreeNode<Token> root = new TreeNode<>(BinaryOperatorToken.ADD);
        root.addChild(new TreeNode<>(FunctionToken.MAX));
        TreeNode<Token> child = new TreeNode<>(UnaryOperatorToken.PLUS);
        root.addChild(child);
        child.addChild(new TreeNode<>(FunctionToken.MODULO));

        String expected =
                "ADD" + NEW_LINE +
                        "├── MAX" + NEW_LINE +
                        "└── PLUS" + NEW_LINE +
                        "    └── MODULO" + NEW_LINE;

        assertEquals(expected, TreePrinter.print(root));
    }

    @Test
    void givenNestedChildInMiddleNode_whenPrint_thenCorrectPointersAreUsed() {
        TreeNode<Token> root = new TreeNode<>(BinaryOperatorToken.ADD);
        TreeNode<Token> child = new TreeNode<>(FunctionToken.MAX);
        root.addChild(child);
        child.addChild(new TreeNode<>(FunctionToken.MODULO));

        root.addChild(new TreeNode<>(UnaryOperatorToken.PLUS));

        String expected =
                "ADD" + NEW_LINE +
                        "├── MAX" + NEW_LINE +
                        "│   └── MODULO" + NEW_LINE +
                        "└── PLUS" + NEW_LINE;

        assertEquals(expected, TreePrinter.print(root));
    }

    @Test
    void givenMultipleNesting_whenPrint_thenCorrectPointersAreUsed() {
        TreeNode<Token> root = new TreeNode<>(BinaryOperatorToken.ADD);
        TreeNode<Token> child = new TreeNode<>(FunctionToken.MAX);
        root.addChild(child);
        TreeNode<Token> nestedChild = new TreeNode<>(FunctionToken.MODULO);
        nestedChild.addChild(new TreeNode<>(UnaryOperatorToken.PLUS));
        child.addChild(nestedChild);

        root.addChild(new TreeNode<>(UnaryOperatorToken.PLUS));

        String expected =
                "ADD" + NEW_LINE +
                        "├── MAX" + NEW_LINE +
                        "│   └── MODULO" + NEW_LINE +
                        "│       └── PLUS" + NEW_LINE +
                        "└── PLUS" + NEW_LINE;

        assertEquals(expected, TreePrinter.print(root));
    }
}