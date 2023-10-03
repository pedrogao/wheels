package github.io.pedrogao.tinybatis.xml;

import java.util.List;

public class MapperNode {
    private String namespace;

    private List<SelectNode> selectNodes;

    public MapperNode(String namespace, List<SelectNode> selectNodes) {
        this.namespace = namespace;
        this.selectNodes = selectNodes;
    }

    public String getNamespace() {
        return namespace;
    }

    public List<SelectNode> getSelectNodes() {
        return selectNodes;
    }
}
