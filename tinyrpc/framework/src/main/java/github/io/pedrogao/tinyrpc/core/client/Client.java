package github.io.pedrogao.tinyrpc.core.client;

import com.alibaba.fastjson.JSON;
import github.io.pedrogao.tinyrpc.core.common.RpcDecoder;
import github.io.pedrogao.tinyrpc.core.common.RpcEncoder;
import github.io.pedrogao.tinyrpc.core.common.RpcInvocation;
import github.io.pedrogao.tinyrpc.core.common.RpcProtocol;
import github.io.pedrogao.tinyrpc.core.common.config.ClientConfig;
import github.io.pedrogao.tinyrpc.core.interfaces.DataService;
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

import static github.io.pedrogao.tinyrpc.core.common.cache.CommonClientCache.SEND_QUEUE;

public class Client {
    private final Logger log = LoggerFactory.getLogger(Client.class);

    private ClientConfig clientConfig;

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }


    public RpcReference startApplication() throws InterruptedException {
        final EventLoopGroup clientGroup = new NioEventLoopGroup();
        final Bootstrap bootstrap = new Bootstrap();

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

        ChannelFuture channelFuture = bootstrap.connect(clientConfig.getServerAddr(), clientConfig.getPort()).sync();
        log.info("Client started and connected to server: {}:{}", clientConfig.getServerAddr(), clientConfig.getPort());
        startClient(channelFuture);

        return new RpcReference(new JDKProxyFactory());
    }

    private void startClient(ChannelFuture channelFuture) {
        Thread asyncSendJob = new Thread(() -> {
            while (true) {
                try {
                    RpcInvocation invocation = SEND_QUEUE.take();
                    RpcProtocol rpcProtocol = new RpcProtocol(JSON.toJSONBytes(invocation));
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
        final ClientConfig clientConfig = new ClientConfig();
        clientConfig.setServerAddr("localhost");
        clientConfig.setPort(1009);
        client.setClientConfig(clientConfig);

        final RpcReference rpcReference = client.startApplication();
        final DataService dataService = rpcReference.get(DataService.class);
        for (int i = 0; i < 100; i++) {
            String response = dataService.sendData("Hello RPC");
            log.info("Client.main: response: {}", response);
        }
    }
}
