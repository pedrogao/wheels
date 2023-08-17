package github.io.pedrogao.tinyrpc.core.server;

import github.io.pedrogao.tinyrpc.core.common.RpcDecoder;
import github.io.pedrogao.tinyrpc.core.common.RpcEncoder;
import github.io.pedrogao.tinyrpc.core.common.config.ServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static github.io.pedrogao.tinyrpc.core.common.cache.CommonServerCache.PROVIDER_CLASS_MAP;

public class Server {
    final Logger log = LoggerFactory.getLogger(Server.class);

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerConfig serverConfig;

    private void startApplication() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.childOption(ChannelOption.SO_SNDBUF, 16 * 1024)
                .option(ChannelOption.SO_RCVBUF, 16 * 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                log.info("initChannel ch: " + channel);

                channel.pipeline().addLast(new RpcEncoder());
                channel.pipeline().addLast(new RpcDecoder());
                channel.pipeline().addLast(new ServerHandler());
            }
        });
        bootstrap.bind(serverConfig.getPort()).sync();
        log.info("Server started!");
    }

    private void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    private void registryService(Object serviceBean) {
        if (serviceBean.getClass().getInterfaces().length == 0) {
            throw new IllegalArgumentException("service must implement interface");
        }
        Class<?>[] classes = serviceBean.getClass().getInterfaces();
        if (classes.length > 1) {
            throw new IllegalArgumentException("service must implement only one interface");
        }

        Class<?> interfaceClass = classes[0];
        PROVIDER_CLASS_MAP.put(interfaceClass.getName(), serviceBean);
    }

    public static void main(String[] args) throws InterruptedException {
        final var server = new Server();
        final ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(1009);

        server.setServerConfig(serverConfig);
        server.registryService(new DataServiceImpl());
        server.startApplication();
    }

}
