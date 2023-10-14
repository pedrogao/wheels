package github.io.pedrogao.tinybatis.xml;

import java.util.List;

public class SelectNode {
    private String id;
    private String resultType;
    private String sql;
    private List<IfNode> ifNodes;

    public SelectNode(String id, String resultType, String sql, List<IfNode> ifNodes) {
        this.id = id;
        this.resultType = resultType;
        this.sql = sql;
        this.ifNodes = ifNodes;
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

    public List<IfNode> getIfNodes() {
        return ifNodes;
    }
}
