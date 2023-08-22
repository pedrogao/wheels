package github.io.pedrogao.tinyrpc.core.client.connection;

import github.io.pedrogao.tinyrpc.core.client.connection.router.RandomRouter;
import github.io.pedrogao.tinyrpc.core.client.connection.router.Router;
import github.io.pedrogao.tinyrpc.core.common.TinyProtocol;
import github.io.pedrogao.tinyrpc.core.common.utils.CommonUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ConnectionManager {
    public static Map<String, List<ConnectionWrapper>> connectionMap = new ConcurrentHashMap<>();

    private final Bootstrap bootstrap;

    private final Router router;

    public ConnectionManager(Bootstrap bootstrap) {
        this(bootstrap, new RandomRouter());
    }

    public ConnectionManager(Bootstrap bootstrap, Router router) {
        this.bootstrap = bootstrap;
        this.router = router;
    }

    private ChannelFuture getChannelFuture(String serviceName) {
        List<ConnectionWrapper> connectionWrappers = connectionMap.get(serviceName);
        if (CommonUtil.isEmptyList(connectionWrappers)) {
            throw new IllegalStateException("channelFutureWrappers is empty");
        }

        ConnectionWrapper selectedConnectionWrapper = router.select(connectionWrappers);
        return selectedConnectionWrapper.getChannelFuture();
    }

    public void connect(String serviceName, String host, int port) throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
        ConnectionWrapper connectionWrapper = new ConnectionWrapper(channelFuture, host, port);

        connectionMap.compute(serviceName, (k, v) -> {
            if (v == null) {
                v = new ArrayList<>();
            }
            v.add(connectionWrapper);
            return v;
        });
    }

    public void disconnect(String serviceName, String host, int port) throws InterruptedException {
        List<ConnectionWrapper> connectionWrappers = connectionMap.get(serviceName);
        if (CommonUtil.isEmptyList(connectionWrappers)) {
            return;
        }

        List<ConnectionWrapper> removeConnectionWrappers = connectionWrappers.stream().
                filter(iter -> iter.getHost().equals(host) && iter.getPort() == port).
                toList();
        for (ConnectionWrapper removeConnectionWrapper : removeConnectionWrappers) {
            removeConnectionWrapper.getChannelFuture().channel().close().sync();
            connectionWrappers.remove(removeConnectionWrapper);
        }
    }

    public ChannelFuture call(String serviceName, TinyProtocol tinyProtocol) {
        return getChannelFuture(serviceName).channel().writeAndFlush(tinyProtocol).syncUninterruptibly();
    }

    public ChannelFuture callAsync(String serviceName, TinyProtocol tinyProtocol) {
        return getChannelFuture(serviceName).channel().writeAndFlush(tinyProtocol);
    }

    public void callAsyncWithTimeout(String serviceName, TinyProtocol tinyProtocol, long timeoutSecond) throws
            ExecutionException, InterruptedException, TimeoutException {
        getChannelFuture(serviceName).channel().writeAndFlush(tinyProtocol).get(timeoutSecond, TimeUnit.SECONDS);
    }

    public void callAsyncWithCallback(String serviceName, TinyProtocol tinyProtocol, GenericFutureListener callback) {
        getChannelFuture(serviceName).channel().writeAndFlush(tinyProtocol).addListener(callback);
    }
}
