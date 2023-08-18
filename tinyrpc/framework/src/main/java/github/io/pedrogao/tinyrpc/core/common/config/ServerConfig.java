package github.io.pedrogao.tinyrpc.core.common.config;

public class ServerConfig {
    private int serverPort;

    private String registerAddr;

    private String applicationName;

    private int registryInterval;

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setRegisterAddr(String registerAddr) {
        this.registerAddr = registerAddr;
    }

    public String getRegisterAddr() {
        return registerAddr;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public int getRegistryInterval() {
        return registryInterval;
    }

    public void setRegistryInterval(int registryInterval) {
        this.registryInterval = registryInterval;
    }
}
