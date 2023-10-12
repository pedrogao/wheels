package github.io.pedrogao.tinybatis.ast;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String input;

    private int index;

    private String currentState;

    private final List<Rule> rules;

    private final List<Token> tokens = new ArrayList<>();

    public static final String START_STATE = "START";

    public static final String END_STATE = "END";

    public static final String CONTINUE_STATE = "CONTINUE";

    public Lexer(String input, List<Rule> rules) {
        this.input = input;
        this.index = 0;
        this.currentState = START_STATE;
        this.rules = rules;
    }

    private void setState(String newState) {
        this.currentState = newState;
    }

    private void consume(String match) {
        index += match.length();
    }

    private String nextToken() {
        if (index >= input.length()) {
            return END_STATE;
        }
        List<Match> allMatches = getAllMatches();
        for (Match match : allMatches) {
            consume(match.matchText);

            if (match.type.equals(TokenType.IGNORE)) {
                return nextToken();
            }

            Token token = new Token(match.matchText, match.index, match.type);
            this.tokens.add(token);
            return CONTINUE_STATE;
        }
        return END_STATE;
    }

    private List<Match> getAllMatches() {
        List<Match> allMatches = new ArrayList<>();
        for (Rule rule : rules) {
            String matchText = rule.match(input.substring(index));
            if (matchText == null) {
                continue;
            }
            var type = rule.getType();
            int index = this.index;
            allMatches.add(new Match(matchText, type, index));
        }
        // sort by longest match descending
        allMatches.sort((o1, o2) -> o2.matchText.length() - o1.matchText.length());
        return allMatches;
    }

    public List<Token> tokenize() {
        reset();

        while (!currentState.equals(END_STATE)) {
            setState(nextToken());
        }

        return tokens;
    }

    public void reset() {
        index = 0;
        currentState = START_STATE;
        tokens.clear();
    }

    static class Match {
        String matchText;
        TokenType type;
        int index;

        Match(String matchText, TokenType type, int index) {
            this.matchText = matchText;
            this.type = type;
            this.index = index;
        }
    }
}
