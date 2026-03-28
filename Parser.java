/**
 * Parser.java
 *
 * Performs recursive descent parsing on a token stream produced by the Lexer.
 * Validates the token stream against the following context-free grammar:
 *
 *   E → E + T | E - T | T
 *   T → T * F | T / F | F
 *   F → (E) | number
 *
 * Because recursive descent cannot directly handle left-recursive rules, each
 * production is rewritten in an equivalent iterative form:
 *
 *   E → T ( ('+' | '-') T )*
 *   T → F ( ('*' | '/') F )*
 *   F → ('-')? ( '(' E ')' | number )
 *
 * In addition to validating the token stream, the parser simultaneously builds
 * an Abstract Syntax Tree (ASTNode) for the expression. A tree printer is
 * included so the caller can display the full trace output required by the spec.
 *
 * Unary minus is supported at the factor level (e.g. -3, -(2+1), --3).
 *
 * @author Chosen Onyejiaka
 * @date   March 2026
 */

import java.util.List;

public class Parser {

    // -----------------------------------------------------------------------
    // Fields
    // -----------------------------------------------------------------------

    /** Flat token stream from the Lexer. */
    private final List<Token> tokens;

    /** Index of the current lookahead token. */
    private int pos;

    /** True only when a full, successful parse has completed. */
    private boolean parseSuccess;

    /** Human-readable error description, or null when parse succeeded. */
    private String errorMessage;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /**
     * Creates a new Parser for the given token list.
     *
     * @param tokens the token stream produced by {@link Lexer#tokenize()}
     */
    public Parser(List<Token> tokens) {
        this.tokens       = tokens;
        this.pos          = 0;
        this.parseSuccess = false;
        this.errorMessage = null;
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Validates the token stream against the grammar and builds the AST.
     * Must be called before {@link #isSuccess()} or {@link #getErrorMessage()}.
     *
     * @return the root {@link ASTNode} of the expression tree, or {@code null}
     *         on a syntax error
     */
    public ASTNode parse() {
        // Nothing to parse
        if (tokens.isEmpty()) {
            errorMessage = "Syntax Error — Empty expression";
            parseSuccess = false;
            return null;
        }

        try {
            ASTNode root = parseExpression();

            // After a valid expression all tokens must be consumed
            if (pos < tokens.size()) {
                Token extra = tokens.get(pos);
                throw new RuntimeException(
                    "Syntax Error — Unexpected token '" + extra.value
                    + "' at position " + extra.position
                    + "\n         Expected: end of expression or operator"
                );
            }

            parseSuccess = true;
            return root;

        } catch (RuntimeException e) {
            errorMessage = e.getMessage();
            parseSuccess = false;
            return null;
        }
    }

    /**
     * Returns {@code true} if the last call to {@link #parse()} succeeded.
     */
    public boolean isSuccess() {
        return parseSuccess;
    }

    /**
     * Returns the error message from the last failed parse, or {@code null}
     * if the parse has not been run or succeeded.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    // -----------------------------------------------------------------------
    // Tree printer
    // -----------------------------------------------------------------------

    /**
     * Prints a visual representation of the AST to standard output using a
     * rotated layout: the root appears at the left margin; the right subtree
     * prints above it and the left subtree below it, each indented one level.
     *
     * Example for (3 + 2) * 5 - 1 evaluating to 24:
     *
     *        1
     * -
     *              5
     *        *
     *                     2
     *              +
     *                     3
     *
     * This clearly shows the operator hierarchy and is easy to produce in a
     * console without a graphics library.
     *
     * @param root the root of the tree to display
     */
    public void printTree(ASTNode root) {
        if (root == null) {
            System.out.println("(empty tree)");
            return;
        }
        printTreeHelper(root, 0);
    }

    /**
     * Recursive helper for {@link #printTree}.
     * Traverses right → node → left so that the tree reads top-to-bottom
     * in the correct order when displayed with indentation.
     *
     * @param node  the current node
     * @param level depth from the root (root = 0)
     */
    private void printTreeHelper(ASTNode node, int level) {
        if (node == null) return;

        // Right child first — will appear above the current node
        printTreeHelper(node.right, level + 1);

        // Current node indented by its depth
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < level; i++) {
            indent.append("       ");   // 7 spaces per level
        }
        System.out.println(indent + node.value);

        // Left child last — will appear below the current node
        printTreeHelper(node.left, level + 1);
    }

    // -----------------------------------------------------------------------
    // Grammar rules — recursive descent
    // -----------------------------------------------------------------------

    /**
     * E → T ( ('+' | '-') T )*
     *
     * Handles additive operators with left-to-right associativity by folding
     * the growing subtree back into the left child at each step.
     *
     * @return the root ASTNode for this expression
     */
    private ASTNode parseExpression() {
        ASTNode left = parseTerm();

        while (pos < tokens.size()
               && (peek().type == Token.Type.PLUS
                   || peek().type == Token.Type.MINUS)) {

            Token   op     = consume();      // consume '+' or '-'
            ASTNode right  = parseTerm();

            ASTNode opNode = new ASTNode(op.value);
            opNode.left    = left;
            opNode.right   = right;
            left           = opNode;         // left-fold to ensure left-associativity
        }

        return left;
    }

    /**
     * T → F ( ('*' | '/') F )*
     *
     * Handles multiplicative operators.  Parsed at a deeper level than
     * additive operators, which naturally enforces their higher precedence.
     *
     * @return the root ASTNode for this term
     */
    private ASTNode parseTerm() {
        ASTNode left = parseFactor();

        while (pos < tokens.size()
               && (peek().type == Token.Type.STAR
                   || peek().type == Token.Type.SLASH)) {

            Token   op    = consume();       // consume '*' or '/'
            ASTNode right = parseFactor();

            ASTNode opNode = new ASTNode(op.value);
            opNode.left    = left;
            opNode.right   = right;
            left           = opNode;
        }

        return left;
    }

    /**
     * F → ('-')? ( '(' E ')' | number )
     *
     * Handles the three base cases:
     *   1. Unary minus      — e.g. -3, -(2+1)
     *   2. Parenthesised expression — e.g. (E)
     *   3. Integer literal  — e.g. 42
     *
     * @return a leaf ASTNode (number or paren group) or a 'negate' unary node
     */
    private ASTNode parseFactor() {

        // ── Unary minus ────────────────────────────────────────────────────
        if (pos < tokens.size() && peek().type == Token.Type.MINUS) {
            Token unaryOp = consume();       // consume the unary '-'

            // Guard: expression cannot end right after a unary minus
            if (pos >= tokens.size()) {
                throw new RuntimeException(
                    "Syntax Error — Unexpected end of expression after '-' at position "
                    + unaryOp.position
                    + "\n         Expected: number or '('"
                );
            }

            // Recurse so that --3 or -(expr) are handled correctly
            ASTNode operand = parseFactor();
            ASTNode negate  = new ASTNode("negate");
            negate.left     = operand;
            return negate;
        }

        // ── Parenthesised sub-expression ───────────────────────────────────
        if (pos < tokens.size() && peek().type == Token.Type.LPAREN) {
            Token lparen = consume();        // consume '('

            // Guard: detect empty parentheses "()"
            if (pos < tokens.size() && peek().type == Token.Type.RPAREN) {
                Token rp = tokens.get(pos);
                throw new RuntimeException(
                    "Syntax Error — Empty parentheses at position " + rp.position
                    + "\n         Expected: number or expression"
                );
            }

            ASTNode inner = parseExpression();   // parse the inner E

            // Expect the matching closing ')'
            if (pos >= tokens.size() || peek().type != Token.Type.RPAREN) {
                int    errPos = (pos < tokens.size()) ? peek().position
                                                      : lparen.position + 1;
                String found  = (pos < tokens.size()) ? "'" + peek().value + "'"
                                                      : "end of expression";
                throw new RuntimeException(
                    "Syntax Error — Missing closing ')'; found " + found
                    + " at position " + errPos
                );
            }
            consume();                           // consume ')'

            return inner;
        }

        // ── Integer literal ────────────────────────────────────────────────
        if (pos < tokens.size() && peek().type == Token.Type.NUMBER) {
            Token num = consume();               // consume the number token
            return new ASTNode(num.value);
        }

        // ── Nothing matched — report a precise error ───────────────────────
        if (pos >= tokens.size()) {
            throw new RuntimeException(
                "Syntax Error — Unexpected end of expression"
                + "\n         Expected: number or '('"
            );
        }

        Token bad = peek();
        throw new RuntimeException(
            "Syntax Error — Unexpected token '" + bad.value
            + "' at position " + bad.position
            + "\n         Expected: number or '('"
        );
    }

    // -----------------------------------------------------------------------
    // Token stream helpers
    // -----------------------------------------------------------------------

    /**
     * Returns the current lookahead token without advancing the position.
     *
     * @return the token at {@code pos}
     */
    private Token peek() {
        return tokens.get(pos);
    }

    /**
     * Returns the current lookahead token and advances {@code pos} by one.
     *
     * @return the token that was at {@code pos} before advancing
     */
    private Token consume() {
        return tokens.get(pos++);
    }
}