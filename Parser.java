/**
 * Parser.java
 *
 * Recursive descent parser that validates the token stream against
 * the context-free grammar and constructs an Abstract Syntax Tree.
 *
 * Grammar:
 *   E → E + T | E - T | T
 *   T → T * F | T / F | F
 *   F → (E) | number
 *
 * @author Chosen Onyejiaka
 * @date March 2026
 */

import java.util.List;

public class Parser {

    private final List<Token> tokens;  // token stream from the Lexer
    private int pos;                   // current position in the token list

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
    }

    // entry point — kicks off the parse and returns the root of the AST
    public ASTNode parse() {
        return parseExpression();
    }

    // E → T (+ or - T)*
    private ASTNode parseExpression() {
        ASTNode left = parseTerm();

        while (pos < tokens.size() &&
              (tokens.get(pos).type == Token.Type.PLUS ||
               tokens.get(pos).type == Token.Type.MINUS)) {

            String op = tokens.get(pos).value;
            pos++;

            ASTNode right = parseTerm();

            ASTNode node = new ASTNode(op);
            node.left  = left;
            node.right = right;
            left = node;
        }

        return left;
    }

    // T → F (* or / F)*
    private ASTNode parseTerm() {
        ASTNode left = parseFactor();

        while (pos < tokens.size() &&
              (tokens.get(pos).type == Token.Type.STAR ||
               tokens.get(pos).type == Token.Type.SLASH)) {

            String op = tokens.get(pos).value;
            pos++;

            ASTNode right = parseFactor();

            ASTNode node = new ASTNode(op);
            node.left  = left;
            node.right = right;
            left = node;
        }

        return left;
    }

    // F → (E) | number
    private ASTNode parseFactor() {
        Token current = tokens.get(pos);

        // parenthesized expression — recurse back into parseExpression
        if (current.type == Token.Type.LPAREN) {
            pos++; // consume '('
            ASTNode node = parseExpression();

            // expect closing paren
            if (pos >= tokens.size() || tokens.get(pos).type != Token.Type.RPAREN) {
                throw new RuntimeException("Syntax Error — Expected ')' at position " + pos);
            }
            pos++; // consume ')'
            return node;
        }

        // plain number — leaf node
        if (current.type == Token.Type.NUMBER) {
            pos++;
            return new ASTNode(current.value);
        }

        // anything else is an error
        throw new RuntimeException("Syntax Error — Unexpected token '" 
            + current.value + "' at position " + current.position);
    }
}
