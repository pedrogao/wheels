package github.io.pedrogao.tinyrpc.core.client.connection;

import github.io.pedrogao.tinyrpc.core.common.utils.CommonUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public static Map<String, List<ConnectionWrapper>> connectionMap = new ConcurrentHashMap<>();

    private final Bootstrap bootstrap;

    public ConnectionManager(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public ChannelFuture getChannelFuture(String serviceName) {
        List<ConnectionWrapper> connectionWrappers = connectionMap.get(serviceName);
        if (CommonUtil.isEmptyList(connectionWrappers)) {
            throw new IllegalStateException("channelFutureWrappers is empty");
        }
        // current random
        // TODO: load balance
        return connectionWrappers.get(new Random().nextInt(connectionWrappers.size())).getChannelFuture();
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
}
