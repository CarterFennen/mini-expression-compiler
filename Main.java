/**
 * Main.java
 *
 * Entry point for the Mini Expression Compiler.
 * Wires together the Lexer, Parser, and Evaluator into a full compiler pipeline.
 * Prints the token stream, parse result, AST, and evaluated result.
 *
 * @author Carter Fennen
 * @date March 2026
 */

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter an expression: ");
        String input = scanner.nextLine();

        try {
            // Phase 1 — Lexical Analysis
            Lexer lexer = new Lexer(input);
            List<Token> tokens = lexer.tokenize();
            System.out.println("\nTokens: " + tokens);

            // Phase 2 — Parsing + AST Construction
            Parser parser = new Parser(tokens);
            ASTNode root = parser.parse();
            System.out.println("\nParse Result: SUCCESS");

            // Phase 3 — Print AST
            System.out.println("\nParse Tree:");
            printTree(root, "", true);

            // Phase 4 — Evaluation
            Evaluator evaluator = new Evaluator();
            int result = evaluator.evaluate(root);
            System.out.println("\nEvaluation Result: " + result);

        } catch (RuntimeException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    // recursively prints the AST in a readable tree format
    private static void printTree(ASTNode node, String indent, boolean isRight) {
        if (node == null) return;

        System.out.println(indent + (isRight ? "└── " : "├── ") + node.value);
        if (node.left != null || node.right != null) {
            printTree(node.right, indent + (isRight ? "    " : "│   "), true);
            printTree(node.left,  indent + (isRight ? "    " : "│   "), false);
        }
    }
}
