package github.io.pedrogao.tinybatis.ast;

import junit.framework.TestCase;

public class RuleTest extends TestCase {

    public void testMatches() {
        Rule rule = Rule.regex(TokenType.BANG_EQUAL, "!=");
        assertTrue(rule.matches("!="));
        assertFalse(rule.matches("=="));
    }

    public void testMatch() {
        Rule rule = Rule.regex(TokenType.BANG_EQUAL, "!=");
        String match = rule.match("!=");
        assertEquals(match, "!=");
    }
}