package github.io.pedrogao.tinybatis.ast;

import junit.framework.TestCase;

import java.util.List;

public class ParserTest extends TestCase {

    public void testParseExpr() {
        Lexer lexer = new Lexer("title != null", Rule.DEFAULT_RULES);
        List<Token> tokens = lexer.tokenize();
        assertEquals(tokens.size(), 3);

        Parser parser = new Parser(tokens);
        Expr expr = parser.parseExpr();

        Expr.Binary binary = (Expr.Binary) expr;
        assertEquals(binary.left, new Expr.Variable(new Token("title", 0, TokenType.IDENTIFIER)));
        assertEquals(binary.operator, new Token("!=", 6, TokenType.BANG_EQUAL));
        assertEquals(binary.right, new Expr.Literal(null));
    }
}