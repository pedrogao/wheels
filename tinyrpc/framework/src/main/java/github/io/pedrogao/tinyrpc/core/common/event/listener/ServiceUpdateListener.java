package github.io.pedrogao.tinyrpc.core.common.event.listener;

import github.io.pedrogao.tinyrpc.core.common.ChannelFutureWrapper;
import github.io.pedrogao.tinyrpc.core.common.event.Listener;
import github.io.pedrogao.tinyrpc.core.common.event.UpdateEvent;
import github.io.pedrogao.tinyrpc.core.common.event.data.URLChangeWrapper;
import github.io.pedrogao.tinyrpc.core.common.utils.CommonUtils;
import github.io.pedrogao.tinyrpc.core.client.ConnectionHandler;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static github.io.pedrogao.tinyrpc.core.common.cache.CommonClientCache.CONNECT_MAP;

public class ServiceUpdateListener implements Listener<UpdateEvent> {

    private final Logger log = LoggerFactory.getLogger(ServiceUpdateListener.class);

    @Override
    public void callback(Object t) {
        // 获取到字节点的数据信息
        URLChangeWrapper urlChangeWrapper = (URLChangeWrapper) t;
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(urlChangeWrapper.getServiceName());
        if (CommonUtils.isEmptyList(channelFutureWrappers)) {
            log.error("[ServiceUpdateListener] channelFutureWrappers is empty");
            return;
        }
        List<String> matchProviderUrl = urlChangeWrapper.getProviderUrl();
        Set<String> finalUrl = new HashSet<>();
        List<ChannelFutureWrapper> finalChannelFutureWrappers = new ArrayList<>();
        for (ChannelFutureWrapper channelFutureWrapper : channelFutureWrappers) {
            String oldServerAddress = channelFutureWrapper.getHost() + ":" + channelFutureWrapper.getPort();
            // 如果老的url没有，说明已经被移除了
            if (!matchProviderUrl.contains(oldServerAddress)) {
                continue;
            }
            finalChannelFutureWrappers.add(channelFutureWrapper);
            finalUrl.add(oldServerAddress);
        }
        // 此时老的url已经被移除了，开始检查是否有新的url
        // ChannelFutureWrapper其实是一个自定义的包装类，将netty建立好的ChannelFuture做了一些封装
        List<ChannelFutureWrapper> newChannelFutureWrapper = new ArrayList<>();
        for (String newProviderUrl : matchProviderUrl) {
            if (!finalUrl.contains(newProviderUrl)) {
                ChannelFutureWrapper channelFutureWrapper = new ChannelFutureWrapper();
                String host = newProviderUrl.split(":")[0];
                int port = Integer.parseInt(newProviderUrl.split(":")[1]);
                channelFutureWrapper.setPort(port);
                channelFutureWrapper.setHost(host);
                ChannelFuture channelFuture;
                try {
                    channelFuture = ConnectionHandler.createChannelFuture(host, port);
                    channelFutureWrapper.setChannelFuture(channelFuture);
                    newChannelFutureWrapper.add(channelFutureWrapper);
                    finalUrl.add(newProviderUrl);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        finalChannelFutureWrappers.addAll(newChannelFutureWrapper);
        // 最终更新服务在这里
        CONNECT_MAP.put(urlChangeWrapper.getServiceName(), finalChannelFutureWrappers);
    }
}
