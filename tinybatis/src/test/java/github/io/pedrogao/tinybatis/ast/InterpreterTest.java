package github.io.pedrogao.tinybatis.ast;

import junit.framework.TestCase;

import java.util.List;
import java.util.Map;

public class InterpreterTest extends TestCase {

    public void testInterpret() {
        Expr expr = new Expr.Binary(
                new Expr.Literal(123d),
                new Token("+", 1, TokenType.PLUS),
                new Expr.Literal(45d)
        );

        Interpreter interpreter = new Interpreter();
        Object result = interpreter.interpret(expr);
        assertEquals(168d, result);
    }

    public void testInterpret1() {
        Lexer lexer = new Lexer("title != null", Rule.DEFAULT_RULES);
        List<Token> tokens = lexer.tokenize();
        assertEquals(tokens.size(), 3);

        Parser parser = new Parser(tokens);
        Expr expr = parser.parseExpr();

        Expr.Binary binary = (Expr.Binary) expr;
        assertEquals(binary.left, new Expr.Variable(new Token("title", 0, TokenType.IDENTIFIER)));
        assertEquals(binary.operator, new Token("!=", 6, TokenType.BANG_EQUAL));
        assertEquals(binary.right, new Expr.Literal(null));

        Interpreter interpreter = new Interpreter();
        Object result = interpreter.interpret(expr, Map.of("title", "Hello World"));
        assertEquals(true, result);
    }
}