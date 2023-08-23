package github.io.pedrogao.tinyrpc.core.common.cache;

import github.io.pedrogao.tinyrpc.core.common.Invocation;
import github.io.pedrogao.tinyrpc.core.common.filter.ClientFilter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommonClientCache {
    public static final BlockingQueue<Invocation> SEND_QUEUE = new ArrayBlockingQueue<>(100);

    public static final Map<String, Object> RESP_MAP = new ConcurrentHashMap<>();

    public static final List<String> SUBSCRIBE_SERVICE_LIST = new CopyOnWriteArrayList<>();

    public static final List<ClientFilter> FILTER_LIST = new CopyOnWriteArrayList<>();
}
