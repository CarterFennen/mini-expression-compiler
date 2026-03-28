/**
 * Evaluator.java
 *
 * Traverses the Abstract Syntax Tree produced by the Parser
 * and recursively computes the final result of the expression.
 *
 * @author Carter Fennen
 * @date March 2026
 */

public class Evaluator {

    // entry point — pass in the root node of the AST
    public int evaluate(ASTNode node) {

        // base case — leaf node, no children, just a number
        if (node.left == null && node.right == null) {
            return Integer.parseInt(node.value);
        }

        // recursive case — evaluate both subtrees first
        int left  = evaluate(node.left);
        int right = evaluate(node.right);

        // apply the operator at this node
        switch (node.value) {
            case "+" -> { return left + right; }
            case "-" -> { return left - right; }
            case "*" -> { return left * right; }
            case "/" -> {
                if (right == 0) {
                    throw new RuntimeException("Math Error — Division by zero at position ");
                }
                return left / right;
            }
            default -> throw new RuntimeException("Unknown operator: " + node.value);
        }
    }
}
