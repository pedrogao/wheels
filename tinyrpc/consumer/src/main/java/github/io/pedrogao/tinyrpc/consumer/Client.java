package github.io.pedrogao.tinyrpc.consumer;

import github.io.pedrogao.tinyrpc.core.client.LogFilter;
import github.io.pedrogao.tinyrpc.core.client.connection.ConnectionManager;
import github.io.pedrogao.tinyrpc.core.common.filter.ClientFilter;
import github.io.pedrogao.tinyrpc.core.common.handler.InvocationSender;
import github.io.pedrogao.tinyrpc.core.common.protocol.SerializationType;
import github.io.pedrogao.tinyrpc.core.common.protocol.TinyDecoder;
import github.io.pedrogao.tinyrpc.core.common.protocol.TinyEncoder;
import github.io.pedrogao.tinyrpc.core.common.Invocation;
import github.io.pedrogao.tinyrpc.core.common.protocol.TinyProtocol;
import github.io.pedrogao.tinyrpc.core.common.config.ClientConfig;
import github.io.pedrogao.tinyrpc.core.common.config.PropertiesBootstrap;
import github.io.pedrogao.tinyrpc.core.common.serialization.Serializer;
import github.io.pedrogao.tinyrpc.core.common.utils.CommonUtil;
import github.io.pedrogao.tinyrpc.core.proxy.ProxyFactory;
import github.io.pedrogao.tinyrpc.core.registry.URL;
import github.io.pedrogao.tinyrpc.core.registry.zookeeper.AbstractRegister;
import github.io.pedrogao.tinyrpc.core.registry.zookeeper.ZookeeperRegister;
import github.io.pedrogao.tinyrpc.interfaces.DataService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static github.io.pedrogao.tinyrpc.core.common.cache.CommonClientCache.*;

public class Client {
    private final Logger log = LoggerFactory.getLogger(Client.class);

    private final Bootstrap bootstrap;

    private ClientConfig clientConfig;

    private AbstractRegister abstractRegister;

    private final ConnectionManager connectionManager;

    public Client(ClientConfig config) {
        clientConfig = config;
        bootstrap = new Bootstrap();
        connectionManager = new ConnectionManager(bootstrap);
        abstractRegister = new ZookeeperRegister(config.getRegisterAddr());
    }

    private void initHandlers() {
        final EventLoopGroup clientGroup = new NioEventLoopGroup();
        bootstrap.group(clientGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new TinyEncoder());
                ch.pipeline().addLast(new TinyDecoder());
                ch.pipeline().addLast(new InvocationSender());
            }
        });
    }

    public void subscribeService(Class<?> serviceBean) {
        String serviceName = serviceBean.getName();
        URL url = new URL(clientConfig.getApplicationName(), serviceName, CommonUtil.getIpAddress());
        abstractRegister.subscribe(url);
    }

    public void startApplication() {
        initHandlers();
        connectServer();
        startBackgroundSendTask();
    }

    public void connectServer() {
        for (String providerServiceName : SUBSCRIBE_SERVICE_LIST) {
            List<URL> providerList = abstractRegister.getProviderList(providerServiceName);
            for (URL provider : providerList) {
                try {
                    log.info("Client.connectServer: connect to provider: {}", provider);
                    connectionManager.connect(providerServiceName, provider.getHost(), provider.getPort().get());
                } catch (InterruptedException e) {
                    log.error("Client.connectServer: ", e);
                }
            }
        }
    }

    public void addFilter(ClientFilter filter) {
        FILTER_LIST.add(filter);
    }

    private void startBackgroundSendTask() {
        Thread asyncSendJob = new Thread(() -> {
            while (true) {
                try {
                    Invocation invocation = SEND_QUEUE.take();
                    Serializer serializer = Serializer.getSerializer(SerializationType.json);
                    TinyProtocol tinyProtocol = new TinyProtocol(serializer.serialize(invocation));

                    connectionManager.call(invocation.getTargetServiceName(), tinyProtocol);
                } catch (InterruptedException e) {
                    log.error("Client.asyncSendJob: ", e);
                }
            }
        });
        asyncSendJob.start();
    }

    public static void main(String[] args) throws Throwable {
        final Logger log = LoggerFactory.getLogger(Client.class);

        final ClientConfig config = loadClientConfig();
        final Client client = new Client(config);

        client.subscribeService(DataService.class);
        client.startApplication();
        client.addFilter(new LogFilter());

        final DataService dataService = ProxyFactory.get(DataService.class); // proxy service bean
        for (int i = 0; i < 100; i++) {
            String response = dataService.sendData("Hello RPC");
            log.info("Client.main: response: {}", response);
        }
    }

    public static ClientConfig loadClientConfig() {
        return PropertiesBootstrap.loadClientConfigFromLocal();
    }
}
