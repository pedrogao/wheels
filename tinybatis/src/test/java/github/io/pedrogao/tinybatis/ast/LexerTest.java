package github.io.pedrogao.tinybatis.ast;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class LexerTest extends TestCase {

    public void testTokenize() {
        List<Rule> rules = Arrays.asList(
                Rule.regex(TokenType.IGNORE, "\\/\\/.*"),
                Rule.regex(TokenType.IGNORE, "\\s+"),
                Rule.regex(TokenType.BANG_EQUAL, "!="),
                Rule.regex(TokenType.NULL, "null"),
                Rule.regex(TokenType.IDENTIFIER, "[a-zA-Z]+"),
                Rule.regex(TokenType.NUMBER, "[+-]?\\d+(\\.\\d+)?")
        );
        Lexer lexer = new Lexer("title != null", rules);
        List<Token> tokens = lexer.tokenize();
        assertEquals(tokens.size(), 3);
        assertEquals(tokens.get(0).getText(), "title");
        assertEquals(tokens.get(0).getIndex(), 0);
        assertEquals(tokens.get(0).getType(), TokenType.IDENTIFIER);

        assertEquals(tokens.get(1).getText(), "!=");
        assertEquals(tokens.get(1).getIndex(), 6);
        assertEquals(tokens.get(1).getType(), TokenType.BANG_EQUAL);

        assertEquals(tokens.get(2).getText(), "null");
        assertEquals(tokens.get(2).getIndex(), 9);
        assertEquals(tokens.get(2).getType(), TokenType.NULL);
    }

    public void testReset() {
    }
}