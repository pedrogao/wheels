package github.io.pedrogao.tinyrpc.provider;

import github.io.pedrogao.tinyrpc.core.common.handler.InvocationHandler;
import github.io.pedrogao.tinyrpc.core.common.protocol.TinyDecoder;
import github.io.pedrogao.tinyrpc.core.common.protocol.TinyEncoder;
import github.io.pedrogao.tinyrpc.core.common.config.PropertiesBootstrap;
import github.io.pedrogao.tinyrpc.core.common.config.ServerConfig;
import github.io.pedrogao.tinyrpc.core.common.utils.CommonUtil;
import github.io.pedrogao.tinyrpc.core.registry.RegistryService;
import github.io.pedrogao.tinyrpc.core.registry.URL;
import github.io.pedrogao.tinyrpc.core.registry.zookeeper.ZookeeperRegister;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static github.io.pedrogao.tinyrpc.core.common.cache.CommonServerCache.PROVIDER_CLASS_MAP;
import static github.io.pedrogao.tinyrpc.core.common.cache.CommonServerCache.PROVIDER_URL_SET;

public class Server {
    final Logger log = LoggerFactory.getLogger(Server.class);

    private ServerConfig serverConfig;
    private RegistryService registryService;
    private final ScheduledExecutorService registryScheduler = Executors.newScheduledThreadPool(1);

    public Server(ServerConfig config) {
        this.serverConfig = config;
    }

    private void startApplication() throws InterruptedException {
        registryService = new ZookeeperRegister(serverConfig.getRegisterAddr());
        registerServicesInRegister();
        listen();
    }

    private void listen() throws InterruptedException {
        final EventLoopGroup bossGroup = new NioEventLoopGroup();
        final EventLoopGroup workerGroup = new NioEventLoopGroup();
        final ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);

        bootstrap.childOption(ChannelOption.TCP_NODELAY, true).
                option(ChannelOption.SO_BACKLOG, 1024).
                childOption(ChannelOption.SO_SNDBUF, 16 * 1024).
                option(ChannelOption.SO_RCVBUF, 16 * 1024).
                childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                log.info("initChannel ch: " + channel);

                channel.pipeline().addLast(new TinyEncoder());
                channel.pipeline().addLast(new TinyDecoder());
                channel.pipeline().addLast(new InvocationHandler());
            }
        });
        bootstrap.bind(serverConfig.getServerPort()).sync();
        log.info("Server started!");
    }

    private void registerServicesInRegister() {
        log.info("Register services start");
        registryScheduler.schedule(() -> {
            for (URL url : PROVIDER_URL_SET) {
                registryService.register(url); // register service url to registry center
            }
        }, serverConfig.getRegistryInterval(), TimeUnit.MILLISECONDS);
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void registerService(Object serviceBean) {
        if (serviceBean.getClass().getInterfaces().length == 0) {
            throw new IllegalArgumentException("service must had interfaces!");
        }

        Class<?>[] classes = serviceBean.getClass().getInterfaces();
        if (classes.length > 1) {
            throw new IllegalArgumentException("service must only had one interface!");
        }
        Class<?> interfaceClass = classes[0]; // first implement interface
        PROVIDER_CLASS_MAP.put(interfaceClass.getName(), serviceBean);

        URL url = new URL(serverConfig.getApplicationName(), interfaceClass.getName(), CommonUtil.getIpAddress(), serverConfig.getServerPort());
        PROVIDER_URL_SET.add(url);
    }

    public static void main(String[] args) throws InterruptedException {
        final var config = loadServerConfig(); // load server config
        final var server = new Server(config);

        server.registerService(new DataServiceImpl()); // register data service
        server.startApplication(); // start server application
    }

    public static ServerConfig loadServerConfig() {
        return PropertiesBootstrap.loadServerConfigFromLocal();
    }
}
