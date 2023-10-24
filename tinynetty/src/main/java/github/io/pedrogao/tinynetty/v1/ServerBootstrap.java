package github.io.pedrogao.tinynetty.v1;

// ServerBootstrap类用于引导服务器端配置和启动
public class ServerBootstrap {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelInitializer channelInitializer;

    public ServerBootstrap() {
        // 初始化 bossGroup 和 workerGroup
    }

    public ServerBootstrap group(EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
        return this;
    }

    public ServerBootstrap channel(Class<? extends Channel> channelClass) {
        // 设置服务器通道类型
        return this;
    }

    public ServerBootstrap childHandler(ChannelInitializer channelInitializer) {
        this.channelInitializer = channelInitializer;
        return this;
    }

    public Channel bind(int port) {
        // 创建服务器Channel，绑定端口，并返回
        return new ChannelImpl(port, bossGroup.next(), workerGroup.next(), channelInitializer);
    }
}
