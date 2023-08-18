package github.io.pedrogao.tinyrpc.core.common;

import io.netty.channel.ChannelFuture;

public class ChannelFutureWrapper {
    private ChannelFuture channelFuture;

    private String host;

    private int port;

    public ChannelFutureWrapper() {
    }

    public ChannelFutureWrapper(ChannelFuture channelFuture, String host, int port) {
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
}
