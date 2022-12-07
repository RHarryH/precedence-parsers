package com.avispa.parser.precedence.parser;

import com.avispa.parser.misc.tree.TreeNode;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Rafał Hiszpański
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class ProductionTreeNode<T> extends TreeNode<T> {
    private final int productionId;

    public ProductionTreeNode(T value, int productionId) {
        super(value);
        this.productionId = productionId;
    }

    @Override
    public String toString() {
        return super.toString() + ":" + productionId;
    }
}