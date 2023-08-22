package github.io.pedrogao.tinyrpc.core.client.connection;

import io.netty.channel.ChannelFuture;

public class ConnectionWrapper {
    private static final int DEFAULT_WEIGHT = 100;

    private ChannelFuture channelFuture;

    private String host;

    private int port;

    private int weight = DEFAULT_WEIGHT;

    public ConnectionWrapper(ChannelFuture channelFuture, String host, int port) {
        this.channelFuture = channelFuture;
        this.host = host;
        this.port = port;
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

    public void setChannelFuture(ChannelFuture channelFuture) {
        this.channelFuture = channelFuture;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
