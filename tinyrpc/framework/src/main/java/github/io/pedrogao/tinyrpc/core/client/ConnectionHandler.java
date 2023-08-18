package github.io.pedrogao.tinyrpc.core.client;

import github.io.pedrogao.tinyrpc.core.common.ChannelFutureWrapper;
import github.io.pedrogao.tinyrpc.core.common.utils.CommonUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static github.io.pedrogao.tinyrpc.core.common.cache.CommonClientCache.CONNECT_MAP;
import static github.io.pedrogao.tinyrpc.core.common.cache.CommonClientCache.SERVER_ADDRESS;

public class ConnectionHandler {
    private static Bootstrap bootstrap;

    public static void setBootstrap(Bootstrap bootstrap) {
        ConnectionHandler.bootstrap = bootstrap;
    }

    public static ChannelFuture createChannelFuture(String host, int port) throws InterruptedException {
        return bootstrap.connect(host, port).sync();
    }

    public static ChannelFuture getChannelFuture(String providerServiceName) {
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(providerServiceName);
        if (CommonUtils.isEmptyList(channelFutureWrappers)) {
            throw new IllegalStateException("channelFutureWrappers is empty");
        }
        // current random
        // TODO: load balance
        return channelFutureWrappers.get(new Random().nextInt(channelFutureWrappers.size())).getChannelFuture();
    }

    public static void connect(String providerServiceName, String providerIp) throws InterruptedException {
        if (bootstrap == null) {
            throw new RuntimeException("bootstrap is null");
        }
        if (!providerIp.contains(":"))
            return;

        String[] providerAddress = providerIp.split(":");
        String ip = providerAddress[0];
        int port = Integer.parseInt(providerAddress[1]);

        ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
        ChannelFutureWrapper channelFutureWrapper = new ChannelFutureWrapper(channelFuture, ip, port);
        SERVER_ADDRESS.add(providerIp);

        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(providerServiceName);
        if (CommonUtils.isEmptyList(channelFutureWrappers)) {
            channelFutureWrappers = new ArrayList<>();
        }
        channelFutureWrappers.add(channelFutureWrapper);
        CONNECT_MAP.put(providerServiceName, channelFutureWrappers);
    }

    public static void disconnect(String providerServiceName, String providerIp) {
        SERVER_ADDRESS.remove(providerIp);
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(providerServiceName);
        if (CommonUtils.isEmptyList(channelFutureWrappers)) {
            return;
        }
        channelFutureWrappers.removeIf(channelFutureWrapper -> providerIp.equals(channelFutureWrapper.getHost() + ":" + channelFutureWrapper.getPort()));
    }
}
