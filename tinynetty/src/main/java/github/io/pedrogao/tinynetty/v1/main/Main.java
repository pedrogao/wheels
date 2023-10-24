package github.io.pedrogao.tinynetty.v1.main;

import github.io.pedrogao.tinynetty.v1.*;

// Main类用于演示使用ServerBootstrap
public class Main {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(4);

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(ChannelImpl.class)
                .childHandler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel channel) {
                        channel.pipeline().addLast("handler", new SimpleHandler());
                    }
                });

        Channel serverChannel = serverBootstrap.bind(8080);
    }
}
