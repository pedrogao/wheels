package github.io.pedrogao.tinyrpc.core.common.cache;

import github.io.pedrogao.tinyrpc.core.common.RpcInvocation;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class CommonClientCache {
    public static final BlockingQueue<RpcInvocation> SEND_QUEUE = new ArrayBlockingQueue<>(100);

    public static final Map<String, Object> RESP_MAP = new ConcurrentHashMap<>();
}
