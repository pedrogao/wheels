package github.io.pedrogao.tinyrpc.core.client;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import github.io.pedrogao.tinyrpc.core.common.RpcDecoder;
import github.io.pedrogao.tinyrpc.core.common.RpcEncoder;
import github.io.pedrogao.tinyrpc.core.common.RpcInvocation;
import github.io.pedrogao.tinyrpc.core.common.RpcProtocol;
import github.io.pedrogao.tinyrpc.core.common.config.ClientConfig;
import github.io.pedrogao.tinyrpc.core.common.config.PropertiesBootstrap;
import github.io.pedrogao.tinyrpc.core.common.event.ListenerLoader;
import github.io.pedrogao.tinyrpc.core.common.utils.CommonUtils;
import github.io.pedrogao.tinyrpc.core.proxy.ProxyFactory;
import github.io.pedrogao.tinyrpc.core.proxy.javassist.JavassistProxyFactory;
import github.io.pedrogao.tinyrpc.core.registry.URL;
import github.io.pedrogao.tinyrpc.core.registry.zookeeper.AbstractRegister;
import github.io.pedrogao.tinyrpc.core.registry.zookeeper.ZookeeperRegister;
import github.io.pedrogao.tinyrpc.interfaces.DataService;
import github.io.pedrogao.tinyrpc.core.proxy.jdk.JDKProxyFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static github.io.pedrogao.tinyrpc.core.common.cache.CommonClientCache.SEND_QUEUE;
import static github.io.pedrogao.tinyrpc.core.common.cache.CommonClientCache.SUBSCRIBE_SERVICE_LIST;

public class Client {
    private final Logger log = LoggerFactory.getLogger(Client.class);

    private final Bootstrap bootstrap = new Bootstrap();
    private ClientConfig clientConfig;
    private ListenerLoader listenerLoader;
    private AbstractRegister abstractRegister;

    private ProxyReference proxyReference;

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public ProxyReference getRpcReference() {
        return proxyReference;
    }

    public void initApplication() throws InterruptedException {
        abstractRegister = new ZookeeperRegister(clientConfig.getRegisterAddr());

        initHandlers();

        loadListeners();

        initReference();
    }

    private void initReference() {
        ProxyFactory proxyFactory;
        if (clientConfig.getProxyType().equals("javassist")) {
            proxyFactory = new JavassistProxyFactory();
        } else {
            proxyFactory = new JDKProxyFactory();
        }
        proxyReference = new ProxyReference(proxyFactory);
    }

    private void loadListeners() {
        listenerLoader = new ListenerLoader();
        listenerLoader.init();
    }

    private void loadClientConfig() {
        clientConfig = PropertiesBootstrap.loadClientConfigFromLocal();
    }

    private void initHandlers() {
        final EventLoopGroup clientGroup = new NioEventLoopGroup();
        bootstrap.group(clientGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new RpcEncoder());
                ch.pipeline().addLast(new RpcDecoder());
                ch.pipeline().addLast(new ClientHandler());
            }
        });
    }

    public void subscribeService(Class<?> serviceBean) {
        URL url = new URL(
                clientConfig.getApplicationName(),
                serviceBean.getName(),
                Maps.newHashMap(Map.of("host", CommonUtils.getIpAddress()))
        );
        abstractRegister.subscribe(url);
    }

    public void startApplication() {
        connectServer();
        startBackgroundSendTask();
    }

    public void connectServer() {
        for (String providerServiceName : SUBSCRIBE_SERVICE_LIST) {
            List<String> providerIpList = abstractRegister.getProviderIpList(providerServiceName);

            for (String providerIp : providerIpList) {
                try {
                    ConnectionHandler.connect(providerServiceName, providerIp);
                } catch (InterruptedException e) {
                    log.error("Client.connectServer: ", e);
                }
            }

            URL url = new URL();
            url.setServiceName(providerServiceName);
            abstractRegister.doAfterSubscribe(url);
        }
    }

    private void startBackgroundSendTask() {
        Thread asyncSendJob = new Thread(() -> {
            while (true) {
                try {
                    RpcInvocation invocation = SEND_QUEUE.take();
                    RpcProtocol rpcProtocol = new RpcProtocol(JSON.toJSONBytes(invocation));
                    ChannelFuture channelFuture = ConnectionHandler.getChannelFuture(invocation.getTargetServiceName());
                    channelFuture.channel().writeAndFlush(rpcProtocol);
                } catch (InterruptedException e) {
                    log.error("Client.asyncSendJob: ", e);
                }
            }
        });
        asyncSendJob.start();
    }

    public static void main(String[] args) throws Throwable {
        final Logger log = LoggerFactory.getLogger(Client.class);

        final Client client = new Client();
        client.loadClientConfig();
        client.initApplication();

        client.subscribeService(DataService.class);

        ConnectionHandler.setBootstrap(client.getBootstrap());

        client.startApplication();

        final ProxyReference proxyReference = client.getRpcReference();
        final DataService dataService = proxyReference.get(DataService.class); // proxy service bean

        for (int i = 0; i < 100; i++) {
            String response = dataService.sendData("Hello RPC");
            log.info("Client.main: response: {}", response);
        }
    }
}
