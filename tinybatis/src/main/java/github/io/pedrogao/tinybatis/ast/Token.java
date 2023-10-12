package github.io.pedrogao.tinybatis.ast;

public class Token {
    private String text;

    private int index;

    private TokenType type;

    public Token(String text, int index, TokenType type) {
        this.text = text;
        this.index = index;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public int getIndex() {
        return index;
    }


    public TokenType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Token{" +
                "text='" + text + '\'' +
                ", index=" + index +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Token other)) {
            return false;
        }
        return this.text.equals(other.text) && this.type.equals(other.type) && this.index == other.index;
    }
}
