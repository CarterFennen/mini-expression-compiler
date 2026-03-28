/**
 * ASTNode.java
 *
 * Represents a single node in the Abstract Syntax Tree.
 * Each node is either an operator (internal node) or a number (leaf node).
 *
 * Operator nodes have a left and right child.
 * Number nodes have no children — they are the leaves of the tree.
 *
 * @author Carter Fennen
 * @date March 2026
 */

public class ASTNode {

    public final String value;   // operator (+, -, *, /) or number (3, 42)
    public ASTNode left;         // left child — null if this is a leaf
    public ASTNode right;        // right child — null if this is a leaf

    public ASTNode(String value) {
        this.value = value;
        this.left = null;
        this.right = null;
    }
}
