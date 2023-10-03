package github.io.pedrogao.tinybatis.xml;

import java.util.List;

public class ConfigurationConfig {
    private List<EnvironmentConfig> environmentConfigs;
    private List<MapperConfig> mapperConfigs;
    private List<MapperNode> mapperNodes;

    private String defaultEnvironment;

    public ConfigurationConfig(String defaultEnvironment, List<EnvironmentConfig> environmentConfigs,
                               List<MapperConfig> mapperConfigs, List<MapperNode> mapperNodes) {
        this.defaultEnvironment = defaultEnvironment;
        this.environmentConfigs = environmentConfigs;
        this.mapperConfigs = mapperConfigs;
        this.mapperNodes = mapperNodes;
    }

    public EnvironmentConfig getDefaultEnvironmentConfig() {
        for (EnvironmentConfig environmentConfig : environmentConfigs) {
            if (environmentConfig.getId().equals(defaultEnvironment)) {
                return environmentConfig;
            }
        }
        throw new RuntimeException("default environment not found");
    }

    public String getDefaultEnvironment() {
        return defaultEnvironment;
    }

    public List<EnvironmentConfig> getEnvironments() {
        return environmentConfigs;
    }

    public List<MapperConfig> getMappers() {
        return mapperConfigs;
    }

    public List<MapperNode> getMapperNodes() {
        return mapperNodes;
    }
}
