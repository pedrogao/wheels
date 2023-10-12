package github.io.pedrogao.tinybatis.ast;

import java.util.List;

public class Parser {

    private int current;

    private List<Token> tokens;

    public Parser(List<Token> tokens) {
        this.current = 0;
        this.tokens = tokens;
    }


    public Expr parseExpr() {
        return expression();
    }

    private Expr expression() {
        // eg: title != null
        // eg: title != null && author == null
        if (!match(TokenType.IDENTIFIER)) {
            return null;
        }

        Token identifier = previous();
        if (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL, TokenType.GREATER,
                TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            Token operator = previous();
            if (match(TokenType.NULL)) {
                return new Expr.Binary(
                        new Expr.Variable(identifier),
                        operator,
                        new Expr.Literal(null)
                );
            }
        }
        return null;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    private RuntimeException error(Token peek, String message) {
        return new RuntimeException("Error at " + peek + ": " + message);
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
}
