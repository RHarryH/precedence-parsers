package com.avispa.parser.shuntingyard.output;

import com.avispa.parser.shuntingyard.token.BinaryOperatorToken;
import com.avispa.parser.shuntingyard.token.FunctionToken;
import com.avispa.parser.shuntingyard.token.Operand;
import com.avispa.parser.shuntingyard.token.Token;
import com.avispa.parser.shuntingyard.token.UnaryOperatorToken;
import com.avispa.parser.util.tree.TreeNode;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * @author Rafał Hiszpański
 */
public final class ExpressionTree extends AbstractOutputTransformer<TreeNode<Token>>{

    @Override
    public TreeNode<Token> parse(String expression) {
        List<Token> output = getParser().parse(expression);

        if(output.isEmpty()) {
            return null;
        }

        Deque<TreeNode<Token>> nodeStack = new ArrayDeque<>();
        for(Token token : output) {
            if(token instanceof Operand) {
                nodeStack.push(new TreeNode<>(token));
            } else if(token instanceof UnaryOperatorToken) {
                pushArgumentNode(token, nodeStack, 1);
            } else if(token instanceof BinaryOperatorToken) {
                pushArgumentNode(token, nodeStack, 2);
            } else if(token instanceof FunctionToken) {
                FunctionToken function = (FunctionToken)token;

                pushArgumentNode(token, nodeStack, function.getExpectedArgCount());
            } else {
                String message = String.format("Token %s can't be processed", token);
                throw new IllegalStateException(message);
            }
        }

        return nodeStack.pop();
    }

    private void pushArgumentNode(Token token, Deque<TreeNode<Token>> nodeStack, int args) {
        TreeNode<Token> tokenNode = new TreeNode<>(token);
        for(int i = 0; i < args; i++) {
            tokenNode.addFirstChild(nodeStack.pop());
        }

        nodeStack.push(tokenNode);
    }
}
