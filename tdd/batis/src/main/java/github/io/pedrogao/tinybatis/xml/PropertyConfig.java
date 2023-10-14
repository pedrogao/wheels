package github.io.pedrogao.tinybatis.xml;

public class PropertyConfig {
    private String name;

    private String value;

    public PropertyConfig(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
