package github.io.pedrogao.tinyrpc.core.common.config;

public class ClientConfig {
    private String serverAddr;
    private int port;

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
