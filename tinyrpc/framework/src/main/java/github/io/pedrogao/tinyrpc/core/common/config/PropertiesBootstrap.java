package github.io.pedrogao.tinyrpc.core.common.config;

import java.io.IOException;

public class PropertiesBootstrap {
    public static ServerConfig loadServerConfigFromLocal() {
        try {
            PropertiesLoader.loadConfiguration();
        } catch (IOException e) {
            throw new RuntimeException("load tinyrpc.properties error " + e.getMessage());
        }

        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setServerPort(PropertiesLoader.getInt("tinyrpc.server.port"));
        serverConfig.setApplicationName(PropertiesLoader.getStr("tinyrpc.application.name"));
        serverConfig.setRegisterAddr(PropertiesLoader.getStr("tinyrpc.register.addr"));
        serverConfig.setRegistryInterval(PropertiesLoader.getInt("tinyrpc.registry.interval"));
        return serverConfig;
    }

    public static ClientConfig loadClientConfigFromLocal() {
        try {
            PropertiesLoader.loadConfiguration();
        } catch (IOException e) {
            throw new RuntimeException("load tinyrpc.properties error " + e.getMessage());
        }

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setProxyType(PropertiesLoader.getStr("tinyrpc.client.proxy-type"));
        clientConfig.setApplicationName(PropertiesLoader.getStr("tinyrpc.application.name"));
        clientConfig.setRegisterAddr(PropertiesLoader.getStr("tinyrpc.register.addr"));
        return clientConfig;
    }
}
