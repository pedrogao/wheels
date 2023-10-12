package github.io.pedrogao.tinybatis.ast;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rule {
    public static List<Rule> DEFAULT_RULES = Arrays.asList(
            Rule.regex(TokenType.IGNORE, "\\/\\/.*"),
            Rule.regex(TokenType.IGNORE, "\\s+"),
            Rule.regex(TokenType.BANG_EQUAL, "!="),
            Rule.regex(TokenType.EQUAL_EQUAL, "=="),
            Rule.regex(TokenType.GREATER, ">"),
            Rule.regex(TokenType.GREATER_EQUAL, ">="),
            Rule.regex(TokenType.LESS, "<"),
            Rule.regex(TokenType.LESS_EQUAL, "<="),
            Rule.regex(TokenType.NULL, "null"),
            Rule.regex(TokenType.IDENTIFIER, "[a-zA-Z]+"),
            Rule.regex(TokenType.NUMBER, "[+-]?\\d+(\\.\\d+)?")
    );

    private final TokenType type;
    private final Pattern pattern;

    public Rule(TokenType type, String regex) {
        this.type = type;
        this.pattern = Pattern.compile(regex);
    }

    public Rule(TokenType type, Pattern pattern) {
        this.type = type;
        this.pattern = pattern;
    }

    public TokenType getType() {
        return type;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public boolean matches(String input) {
        return pattern.matcher(input).matches();
    }

    public String match(String input) {
        Matcher matcher = pattern.matcher(input);
        boolean b = matcher.find();
        if (!b) {
            return null;
        }
        // Must match from the beginning
        int start = matcher.start(0);
        if (start != 0)
            return null;
        return matcher.group(0);
    }

    public static Rule regex(TokenType type, String regex) {
        return new Rule(type, regex);
    }

    public static Rule regex(TokenType type, Pattern pattern) {
        return new Rule(type, pattern);
    }
}
