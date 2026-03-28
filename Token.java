/**
 * Token.java
 *
 * Represents a single token produced by the Lexer during lexical analysis.
 * Each token captures its type, raw value, and position in the input string.
 *
 * @author Carter Fennen
 * @date March 2026
 */
public class Token {

    // Represents every valid token type in an arithmetic expression
    public enum Type {
        NUMBER,   // e.g. 3, 42
        PLUS,     // +
        MINUS,    // -
        STAR,     // *
        SLASH,    // /
        LPAREN,   // (
        RPAREN    // )
    }

    public final Type type;       // category of this token
    public final String value;    // the raw character(s) from the input
    public final int position;    // index in the input string, used for error reporting

    // Tokens are immutable — once created by the Lexer, they never change
    public Token(Type type, String value, int position){
        this.type = type;
        this.value = value;
        this.position = position;
    }

    // Prints the raw value, so a list of tokens displays as [(, 3, +, 2, *, 5]
    public String toString(){
        return value;
    }
}
