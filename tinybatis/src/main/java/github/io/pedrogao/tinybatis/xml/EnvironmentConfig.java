package github.io.pedrogao.tinybatis.xml;

public class EnvironmentConfig {
    private String id;

    private TransactionManagerConfig transactionManagerConfig;

    private DataSourceConfig dataSourceConfig;

    public EnvironmentConfig(String id, TransactionManagerConfig transactionManagerConfig, DataSourceConfig dataSourceConfig) {
        this.id = id;
        this.transactionManagerConfig = transactionManagerConfig;
        this.dataSourceConfig = dataSourceConfig;
    }

    public TransactionManagerConfig getTransactionManagerConfig() {
        return transactionManagerConfig;
    }

    public DataSourceConfig getDataSourceConfig() {
        return dataSourceConfig;
    }

    public String getId() {
        return id;
    }
}
