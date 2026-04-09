# Mini Expression Compiler
### From Expression to Evaluation — Java | OOP | Compiler Theory

A Java implementation of a multi-phase compiler component that takes an arithmetic expression as input and processes it through **lexical analysis**, **parsing**, **AST construction**, and **evaluation** — simulating how real-world compilers break down and understand programming instructions.

---

## Project Overview

This project builds a compiler pipeline entirely from scratch using core Java and object-oriented design. No external libraries. Every phase — tokenizing, parsing, tree building, and evaluating — is implemented as a separate, modular component.

| Phase | Component | What It Does |
|---|---|---|
| 1 | **Lexer (Tokenizer)** | Converts raw expression string into a token stream |
| 2 | **Parser** | Validates token stream against a context-free grammar using recursive descent |
| 3 | **AST Builder** | Constructs an abstract syntax tree from the parsed expression |
| 4 | **Evaluator** | Traverses the AST to compute the final result |
| 5 | **Trace Output** | Prints token stream, parse result, tree structure, and evaluated result |

---

## Sample Input / Output

### Successful Expression

**Input:**
```
Expression: (3 + 2) * 5 - 1
```

**Output:**
```
Tokens: [(, 3, +, 2, ), *, 5, -, 1]

Parse Result: SUCCESS

Parse Tree:
        -
       / \
      *   1
     / \
    +   5
   / \
  3   2

Evaluation Result: 24
```

---

### Error Expression

**Input:**
```
Expression: 3 + * 5
```

**Output:**
```
Tokens: [3, +, *]

Parse Result: FAILED

Error: Syntax Error — Unexpected token '*' at position 3
       Expected: number or '('
```

---

## Grammar

The parser validates expressions against the following context-free grammar, which enforces standard operator precedence:
```
E → E + T | E - T | T
T → T * F | T / F | F
F → (E) | number
```

- `E` — Expression (handles + and -)
- `T` — Term (handles * and /)
- `F` — Factor (handles numbers and parenthesized sub-expressions)

This structure ensures `*` and `/` bind more tightly than `+` and `-` without any special-case logic.

---

## Project Structure
```
mini-expression-compiler/
├── src/
│   ├── Token.java          # Token types and token representation
│   ├── Lexer.java          # Tokenizer — converts string to token stream
│   ├── Parser.java         # Recursive descent parser and AST builder
│   ├── ASTNode.java        # Abstract syntax tree node structure
│   ├── Evaluator.java      # AST traversal and expression evaluation
│   └── Main.java           # Entry point — runs the full compiler pipeline
├── test/
│   └── CompilerTest.java   # Test cases for each compiler phase
├── .gitignore
└── README.md
```

---

## How to Run

### Prerequisites
- Java 11 or higher

### Setup
```bash
# Clone the repository
git clone https://github.com/CarterFennen/mini-expression-compiler.git
cd mini-expression-compiler
```

### Compile
```bash
mkdir out
javac -d out src/*.java
```

### Run
```bash
java -cp out Main
```

You will be prompted to enter an arithmetic expression. The program will print the full compiler trace — tokens, parse result, AST, and evaluated result.

---

## Test Cases

| Input | Expected Result |
|---|---|
| `3 + 4 * 2` | `11` (operator precedence enforced) |
| `(3 + 2) * 5 - 1` | `24` |
| `(1 + 2) * (3 + 4)` | `21` |
| `((3))` | `3` (nested parentheses) |
| `-3 + 5` | `2` (unary operator support) |
| `3 + * 5` | `Error: Unexpected token '*' at position 3` |
| `()` | `Error: Empty parentheses at position 1` |
| `3 + (4 - )` | `Error: Unexpected token ')' at position 6` |

### Run Tests
```bash
javac -d out src/*.java test/*.java
java -cp out CompilerTest
```

---

## Error Handling

The compiler produces clear, position-specific error messages for invalid input:
```
Input:   3 + (4 - )
Output:  Syntax Error — Unexpected token ')' at position 6
         Expected: number or expression

Input:   3 + * 5
Output:  Syntax Error — Unexpected token '*' at position 3
         Expected: number or '('
```

---

## Design Decisions

**Why recursive descent parsing?**
Recursive descent maps directly to the grammar rules — each non-terminal (`E`, `T`, `F`) becomes its own method. This makes the parser readable, debuggable, and easy to extend with new grammar rules. It's also how many real-world compilers begin.

**Why separate AST construction from evaluation?**
Keeping the tree builder and evaluator as separate components follows the single responsibility principle. The AST is a complete, inspectable representation of the expression — decoupled from what you do with it. This makes it easy to add new operations (like pretty-printing or optimization passes) without touching the evaluator.

**Why model tokens as objects rather than plain strings?**
A `Token` object carries both the type (NUMBER, OPERATOR, PAREN) and the value, plus position metadata. This makes error messages precise and keeps every downstream component from needing to re-parse raw strings.

**What would we add next?**
- Support for floating point numbers and variables
- A simple GUI that visually renders the AST as a tree diagram in real time as the user types

---

## Key Concepts Demonstrated

- **Lexical Analysis** — finite tokenization of raw input
- **Context-Free Grammar** — formal grammar definition and recursive descent implementation
- **Abstract Syntax Trees** — tree construction, traversal, and evaluation
- **Object-Oriented Design** — encapsulation, modularity, and separation of concerns across phases
- **Error Handling** — position-aware syntax error detection and reporting

---

## Author

**Carter Fennen & Chosen Onyejiaka**
Computer Science — West Chester University of Pennsylvania
[GitHub](https://github.com/carterfennen) • carterfennen@icloud.com
CO1023015@wcupa.edu

---

## Course

CSC 220 — Foundations of Computer Science | West Chester University of Pennsylvania
