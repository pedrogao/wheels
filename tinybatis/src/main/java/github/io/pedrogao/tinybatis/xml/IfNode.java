package github.io.pedrogao.tinybatis.xml;

import github.io.pedrogao.tinybatis.ast.Lexer;
import github.io.pedrogao.tinybatis.ast.Rule;
import github.io.pedrogao.tinybatis.ast.Token;

import java.util.List;

//
// Concat subSql if test condition is true
//
public class IfNode {
    private String test;
    private String subSql;

    private List<Token> tokens;

    public IfNode(String test, String subSql) {
        this.test = test;
        this.subSql = subSql;
        this.tokens = new Lexer(test, Rule.DEFAULT_RULES).tokenize();
    }

    public String getTest() {
        return test;
    }

    public String getSubSql() {
        return subSql;
    }

    public List<Token> getTokens() {
        return tokens;
    }
}
