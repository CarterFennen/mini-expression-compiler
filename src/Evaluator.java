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

        // leaf node — no children, just a number
        if (node.left == null && node.right == null) {
            return Integer.parseInt(node.value);
        }

        // unary minus — negate the left subtree
        if (node.value.equals("negate")) {
            return -evaluate(node.left);
        }

        // binary operators — evaluate both subtrees first
        int left  = evaluate(node.left);
        int right = evaluate(node.right);

        switch (node.value) {
            case "+" -> { return left + right; }
            case "-" -> { return left - right; }
            case "*" -> { return left * right; }
            case "/" -> {
                if (right == 0) throw new RuntimeException("Math Error — Division by zero");
                return left / right;
            }
            default -> throw new RuntimeException("Unknown operator: " + node.value);
        }
    }
}
