package github.io.pedrogao.tinybatis.ast;

public enum TokenType {
    // Single-character tokens.
    LEFT_PAREN, // (
    RIGHT_PAREN, // )
    COMMA, // ,
    DOT, // .
    MINUS, // -
    PLUS, // +
    SEMICOLON, // ;
    SLASH, // /
    STAR, // *

    // One or two character tokens.
    BANG, // !
    BANG_EQUAL, // !=
    EQUAL, // =
    EQUAL_EQUAL, // ==
    GREATER, // >
    GREATER_EQUAL, // >=
    LESS, // <
    LESS_EQUAL, // <=

    // Literals.
    IDENTIFIER, // a-z
    STRING, // 'xx'
    NUMBER, // 0-9

    // Keywords.
    AND, // and
    OR, // or

    NULL, // null

    EOF, // EOF

    IGNORE, // ignore
}
