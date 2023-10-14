package github.io.pedrogao.tinybatis.xml;

import java.util.List;

public class DataSourceConfig {
    private String type;

    private List<PropertyConfig> propertyConfigs;

    public DataSourceConfig(String type, List<PropertyConfig> propertyConfigs) {
        this.type = type;
        this.propertyConfigs = propertyConfigs;
    }

    public String getType() {
        return type;
    }

    public List<PropertyConfig> getPropertyConfigs() {
        return propertyConfigs;
    }

    public String getDriver() {
        return propertyConfigs.stream().filter(propertyConfig -> propertyConfig.getName().equals("driver")).
                findFirst().map(PropertyConfig::getValue).orElseThrow(() -> new RuntimeException("driver not found"));
    }

    public String getUrl() {
        return propertyConfigs.stream().filter(propertyConfig -> propertyConfig.getName().equals("url")).
                findFirst().map(PropertyConfig::getValue).orElseThrow(() -> new RuntimeException("url not found"));
    }

    public String getUsername() {
        return propertyConfigs.stream().filter(propertyConfig -> propertyConfig.getName().equals("username")).
                findFirst().map(PropertyConfig::getValue).orElseThrow(() -> new RuntimeException("username not found"));
    }

    public String password() {
        return propertyConfigs.stream().filter(propertyConfig -> propertyConfig.getName().equals("password")).
                findFirst().map(PropertyConfig::getValue).orElseThrow(() -> new RuntimeException("password not found"));
    }
}
