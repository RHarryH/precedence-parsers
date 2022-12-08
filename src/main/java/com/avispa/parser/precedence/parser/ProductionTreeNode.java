package com.avispa.parser.precedence.parser;

import com.avispa.parser.misc.tree.TreeNode;
import com.avispa.parser.precedence.grammar.Symbol;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Rafał Hiszpański
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class ProductionTreeNode extends TreeNode<Symbol> {
    private final int productionId;

    public ProductionTreeNode(Symbol value, int productionId) {
        super(value);
        this.productionId = productionId;
    }

    @Override
    public String toString() {
        return super.toString() + ":" + productionId;
    }
}