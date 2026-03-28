/**
 * CompilerTest.java
 *
 * Runs automated test cases for each phase of the Mini Expression Compiler.
 * Tests valid expressions, operator precedence, parentheses, and error handling.
 *
 * @author Carter Fennen & Chosen Onyejiaka
 * @date March 2026
 */

public class CompilerTest {

    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) {

        System.out.println("Running Compiler Tests...\n");

        // --- Valid Expressions ---
        testValid("3 + 4 * 2",          11);
        testValid("(3 + 2) * 5 - 1",    24);
        testValid("(1 + 2) * (3 + 4)",  21);
        testValid("((3))",               3);
        testValid("-3 + 5",              2);
        testValid("10 / 2",              5);
        testValid("2 + 3 * 4 - 1",      13);

        // --- Error Cases ---
        testError("3 + * 5",    "Unexpected token");
        testError("()",         "Unexpected token");
        testError("3 + (4 - )", "Unexpected token");

        // --- Summary ---
        System.out.println("----------------------------");
        System.out.println("Results: " + passed + " passed, " + failed + " failed");
    }

    // tests a valid expression and checks the evaluated result
    static void testValid(String input, int expected) {
        try {
            Lexer lexer       = new Lexer(input);
            Parser parser     = new Parser(lexer.tokenize());
            Evaluator eval    = new Evaluator();
            int result        = eval.evaluate(parser.parse());

            if (result == expected) {
                System.out.println("PASS | \"" + input + "\" = " + result);
                passed++;
            } else {
                System.out.println("FAIL | \"" + input + "\" expected " + expected + " but got " + result);
                failed++;
            }
        } catch (Exception e) {
            System.out.println("FAIL | \"" + input + "\" threw unexpected error: " + e.getMessage());
            failed++;
        }
    }

    // tests an invalid expression and checks that an error is thrown
    static void testError(String input, String expectedMessage) {
        try {
            Lexer lexer   = new Lexer(input);
            Parser parser = new Parser(lexer.tokenize());
            parser.parse();
            System.out.println("FAIL | \"" + input + "\" expected error but got none");
            failed++;
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains(expectedMessage.toLowerCase())) {
                System.out.println("PASS | \"" + input + "\" correctly threw error: " + e.getMessage());
                passed++;
            } else {
                System.out.println("FAIL | \"" + input + "\" wrong error: " + e.getMessage());
                failed++;
            }
        }
    }
}
```

---

## Expected output when you run it
```
Running Compiler Tests...

PASS | "3 + 4 * 2" = 11
PASS | "(3 + 2) * 5 - 1" = 24
PASS | "(1 + 2) * (3 + 4)" = 21
PASS | "((3))" = 3
PASS | "-3 + 5" = 2
PASS | "10 / 2" = 5
PASS | "2 + 3 * 4 - 1" = 13
PASS | "3 + * 5" correctly threw error: ...
PASS | "()" correctly threw error: ...
PASS | "3 + (4 - )" correctly threw error: ...
----------------------------
Results: 10 passed, 0 failed
