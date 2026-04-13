import java.util.List;
import java.util.ArrayList;
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
            printTree(root);

            // Phase 4 — Evaluation
            Evaluator evaluator = new Evaluator();
            int result = evaluator.evaluate(root);
            System.out.println("\nEvaluation Result: " + result);

        } catch (RuntimeException e) {
            System.out.println("\nParse Result: FAILED");
            System.out.println("Error: " + e.getMessage());
        }
    }

    // prints the visual tree by building lines then printing them
    private static void printTree(ASTNode root) {
        List<String> lines = buildLines(root);
        for (String line : lines) {
            System.out.println(line);
        }
    }

    // recursively builds the visual tree as a list of strings
    private static List<String> buildLines(ASTNode node) {
        List<String> lines = new ArrayList<>();
        if (node == null) return lines;

        // leaf node — just return the value
        if (node.left == null && node.right == null) {
            lines.add(node.value);
            return lines;
        }

        List<String> leftLines  = buildLines(node.left);
        List<String> rightLines = buildLines(node.right);

        int leftWidth  = leftLines.isEmpty()  ? 0 : leftLines.get(0).length();
        int rightWidth = rightLines.isEmpty() ? 0 : rightLines.get(0).length();

        int totalWidth = leftWidth + 3 + rightWidth;
        int rootPos    = leftWidth + 1;

        // center the root value on this level
        StringBuilder rootLine = new StringBuilder(" ".repeat(totalWidth));
        rootLine.setCharAt(rootPos, node.value.charAt(0));
        lines.add(rootLine.toString());

        // the / \ branch line
        StringBuilder slashes = new StringBuilder(" ".repeat(totalWidth));
        if (rootPos - 1 >= 0)         slashes.setCharAt(rootPos - 1, '/');
        if (rootPos + 1 < totalWidth) slashes.setCharAt(rootPos + 1, '\\');
        lines.add(slashes.toString());

        // zip left and right subtree lines side by side
        int maxLines = Math.max(leftLines.size(), rightLines.size());
        for (int i = 0; i < maxLines; i++) {
            String left  = i < leftLines.size()  ? leftLines.get(i)  : " ".repeat(leftWidth);
            String right = i < rightLines.size() ? rightLines.get(i) : " ".repeat(rightWidth);
            lines.add(left + "   " + right);
        }

        return lines;
    }
}
