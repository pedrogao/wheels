package github.io.pedrogao.tinyrpc.core.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    private static Properties properties;

    private final static String DEFAULT_CONFIG_PATH = "tinyrpc.properties";

    public static void loadConfiguration(String path) throws IOException {
        if (properties != null)
            return;

        properties = new Properties();

        InputStream stream = PropertiesLoader.class.getClassLoader().getResourceAsStream(path);
        properties.load(stream);
    }

    public static void loadConfiguration() throws IOException {
        loadConfiguration(DEFAULT_CONFIG_PATH);
    }

    public static String getStr(String key) {
        return properties.getProperty(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }
}
