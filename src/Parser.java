/**
 * Recursive descent parser — validates token stream and builds an AST.
 * Grammar: E → T((+|-)T)* | T → F((*|/)F)* | F → ('-')? ('('E')' | number)
 *
 * @author Chosen Onyejiaka
 * @date   March 2026
 */
 
import java.util.List;
 
public class Parser {
 
    private final List<Token> tokens;
    private int pos;
    private boolean parseSuccess;
    private String errorMessage;
 
    public Parser(List<Token> tokens) {
        this.tokens       = tokens;
        this.pos          = 0;
        this.parseSuccess = false;
        this.errorMessage = null;
    }
 
    // Runs the parse and returns the AST root, or null on failure
    public ASTNode parse() {
        if (tokens.isEmpty()) {
            errorMessage = "Syntax Error — Empty expression";
            parseSuccess = false;
            return null;
        }
 
        try {
            ASTNode root = parseExpression();
 
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
 
    public boolean isSuccess() {
        return parseSuccess;
    }
 
    public String getErrorMessage() {
        return errorMessage;
    }
 
    // Prints the AST rotated 90° — right subtree on top, left subtree below
    public void printTree(ASTNode root) {
        if (root == null) {
            System.out.println("(empty tree)");
            return;
        }
        printTreeHelper(root, 0);
    }
 
    private void printTreeHelper(ASTNode node, int level) {
        if (node == null) return;
        printTreeHelper(node.right, level + 1);
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < level; i++) indent.append("       ");
        System.out.println(indent + node.value);
        printTreeHelper(node.left, level + 1);
    }
 
    // E → T ( ('+' | '-') T )*
    private ASTNode parseExpression() {
        ASTNode left = parseTerm();
 
        while (pos < tokens.size()
               && (peek().type == Token.Type.PLUS
                   || peek().type == Token.Type.MINUS)) {
 
            Token   op     = consume();
            ASTNode right  = parseTerm();
            ASTNode opNode = new ASTNode(op.value);
            opNode.left    = left;
            opNode.right   = right;
            left           = opNode;
        }
 
        return left;
    }
 
    // T → F ( ('*' | '/') F )*
    private ASTNode parseTerm() {
        ASTNode left = parseFactor();
 
        while (pos < tokens.size()
               && (peek().type == Token.Type.STAR
                   || peek().type == Token.Type.SLASH)) {
 
            Token   op    = consume();
            ASTNode right = parseFactor();
            ASTNode opNode = new ASTNode(op.value);
            opNode.left    = left;
            opNode.right   = right;
            left           = opNode;
        }
 
        return left;
    }
 
    // F → ('-')? ( '(' E ')' | number )
    private ASTNode parseFactor() {
 
        // Unary minus
        if (pos < tokens.size() && peek().type == Token.Type.MINUS) {
            Token unaryOp = consume();
            if (pos >= tokens.size()) {
                throw new RuntimeException(
                    "Syntax Error — Unexpected end of expression after '-' at position "
                    + unaryOp.position + "\n         Expected: number or '('"
                );
            }
            ASTNode operand = parseFactor();
            ASTNode negate  = new ASTNode("negate");
            negate.left     = operand;
            return negate;
        }
 
        // Parenthesised sub-expression
        if (pos < tokens.size() && peek().type == Token.Type.LPAREN) {
            Token lparen = consume();
            if (pos < tokens.size() && peek().type == Token.Type.RPAREN) {
                throw new RuntimeException(
                    "Syntax Error — Empty parentheses at position " + tokens.get(pos).position
                    + "\n         Expected: number or expression"
                );
            }
            ASTNode inner = parseExpression();
            if (pos >= tokens.size() || peek().type != Token.Type.RPAREN) {
                int    errPos = (pos < tokens.size()) ? peek().position : lparen.position + 1;
                String found  = (pos < tokens.size()) ? "'" + peek().value + "'" : "end of expression";
                throw new RuntimeException(
                    "Syntax Error — Missing closing ')'; found " + found + " at position " + errPos
                );
            }
            consume();
            return inner;
        }
 
        // Number literal
        if (pos < tokens.size() && peek().type == Token.Type.NUMBER) {
            return new ASTNode(consume().value);
        }
 
        // Nothing matched
        if (pos >= tokens.size()) {
            throw new RuntimeException(
                "Syntax Error — Unexpected end of expression\n         Expected: number or '('"
            );
        }
        Token bad = peek();
        throw new RuntimeException(
            "Syntax Error — Unexpected token '" + bad.value
            + "' at position " + bad.position + "\n         Expected: number or '('"
        );
    }
 
    private Token peek()    { return tokens.get(pos); }
    private Token consume() { return tokens.get(pos++); }
}