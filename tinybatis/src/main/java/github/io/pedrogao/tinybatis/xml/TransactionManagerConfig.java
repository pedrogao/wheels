package github.io.pedrogao.tinybatis.xml;

public class TransactionManagerConfig {
    private String type;

    public TransactionManagerConfig(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
