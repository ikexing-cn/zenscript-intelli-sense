package raylras.zen.code.tree;

import raylras.zen.code.Range;

/**
 * Base class of all Abstract Syntax Tree (AST) nodes.
 * An AST node represents a source code construct, such as a name,
 * type, expression, statement, or declaration.
 */
public abstract class TreeNode {

    public Range range;

    public TreeNode(Range range) {
        this.range = range;
    }

    public abstract <R> R accept(TreeVisitor<R> visitor);

}