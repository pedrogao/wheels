package github.io.pedrogao.tinybatis.session;

import github.io.pedrogao.tinybatis.xml.ConfigurationConfig;
import github.io.pedrogao.tinybatis.xml.MapperNode;
import github.io.pedrogao.tinybatis.xml.SelectNode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SqlSessionFactory {
    private ConfigurationConfig configurationConfig;

    private Map<String, SelectNode> selectNodeMap;

    public SqlSessionFactory(ConfigurationConfig configurationConfig) throws ClassNotFoundException {
        this.configurationConfig = configurationConfig;
        this.selectNodeMap = new ConcurrentHashMap<>();
        loadDrivers();
        loadMappers();
    }

    private void loadMappers() {
        for (MapperNode mapperNode : configurationConfig.getMapperNodes()) {
            for (SelectNode selectNode : mapperNode.getSelectNodes()) {
                String id = mapperNode.getNamespace() + "." + selectNode.getId();
                selectNodeMap.put(id, selectNode);
            }
        }
    }

    private void loadDrivers() throws ClassNotFoundException {
        String driver = configurationConfig.getDefaultEnvironmentConfig().getDataSourceConfig().getDriver();
        Class.forName(driver);
    }

    public SqlSession openSession() {
        String url = configurationConfig.getDefaultEnvironmentConfig().getDataSourceConfig().getUrl();
        String user = configurationConfig.getDefaultEnvironmentConfig().getDataSourceConfig().getUsername();
        String password = configurationConfig.getDefaultEnvironmentConfig().getDataSourceConfig().password();
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            return new SqlSession(connection, selectNodeMap);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
