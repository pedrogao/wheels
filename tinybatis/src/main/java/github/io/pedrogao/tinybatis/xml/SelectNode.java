package github.io.pedrogao.tinybatis.xml;

public class SelectNode {
    private String id;
    private String resultType;
    private String sql;

    public SelectNode(String id, String resultType, String sql) {
        this.id = id;
        this.resultType = resultType;
        this.sql = sql;
    }

    public String getId() {
        return id;
    }

    public String getResultType() {
        return resultType;
    }

    public String getSql() {
        return sql;
    }
}
