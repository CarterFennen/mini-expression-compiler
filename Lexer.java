/**
 * Lexer.java
 *
 * Performs lexical analysis on a raw arithmetic expression string.
 * Walks the input character by character and produces a list of Token objects
 * for the Parser to consume.
 *
 * @author Carter Fennen
 * @date March 2026
 */

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private final String input;  // the raw expression string
    private int pos;             // current position in the input

    public Lexer(String input) {
        this.input = input;
        this.pos = 0;
    }

    // Walks the input and returns a complete list of tokens
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (pos < input.length()) {
            char c = input.charAt(pos);

            // Skip whitespace — spaces don't affect the expression
            if (Character.isWhitespace(c)) {
                pos++;
                continue;
            }

            // Multi-digit number handling — keep reading digits until a non-digit is hit
            if (Character.isDigit(c)) {
                int start = pos;
                while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
                    pos++;
                }
                tokens.add(new Token(Token.Type.NUMBER, input.substring(start, pos), start));
                continue;
            }

            // Single character operators and parentheses
            switch (c) {
                case '+' -> tokens.add(new Token(Token.Type.PLUS,   "+", pos++));
                case '-' -> tokens.add(new Token(Token.Type.MINUS,  "-", pos++));
                case '*' -> tokens.add(new Token(Token.Type.STAR,   "*", pos++));
                case '/' -> tokens.add(new Token(Token.Type.SLASH,  "/", pos++));
                case '(' -> tokens.add(new Token(Token.Type.LPAREN, "(", pos++));
                case ')' -> tokens.add(new Token(Token.Type.RPAREN, ")", pos++));
                default  -> throw new RuntimeException("Unexpected character '" + c + "' at position " + pos);
            }
        }

        return tokens;
    }
}
