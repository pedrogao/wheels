package github.io.pedrogao.tinyrpc.core.common.cache;

import github.io.pedrogao.tinyrpc.core.common.ChannelFutureWrapper;
import github.io.pedrogao.tinyrpc.core.common.RpcInvocation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommonClientCache {
    public static final BlockingQueue<RpcInvocation> SEND_QUEUE = new ArrayBlockingQueue<>(100);

    public static final Map<String, Object> RESP_MAP = new ConcurrentHashMap<>();

    public static final List<String> SUBSCRIBE_SERVICE_LIST = new CopyOnWriteArrayList<>();

    public static Map<String, List<ChannelFutureWrapper>> CONNECT_MAP = new ConcurrentHashMap<>();

    public static final List<String> SERVER_ADDRESS = new CopyOnWriteArrayList<>();
}
