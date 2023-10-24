package github.io.pedrogao.tinynetty.v1;

// ChannelInitializer用于初始化新连接的Channel
public abstract class ChannelInitializer {
    protected abstract void initChannel(Channel channel);
}
