package github.io.pedrogao.tinyrpc.core.proxy.jdk;

import github.io.pedrogao.tinyrpc.core.common.Invocation;
import github.io.pedrogao.tinyrpc.core.common.cache.CommonClientCache;
import github.io.pedrogao.tinyrpc.core.common.filter.ClientFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static github.io.pedrogao.tinyrpc.core.common.cache.CommonClientCache.RESP_MAP;
import static github.io.pedrogao.tinyrpc.core.common.cache.CommonClientCache.SEND_QUEUE;

public class JDKClientInvocationHandler implements InvocationHandler {
    private final Logger log = LoggerFactory.getLogger(JDKClientInvocationHandler.class);

    private final static Object OBJECT = new Object();

    private Class<?> clazz;

    public JDKClientInvocationHandler(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Invocation invocation = new Invocation(method.getName(), clazz.getName(), args, UUID.randomUUID().toString());

        boolean ok = doFilter(invocation);
        if (!ok) {
            log.error("JDKClientInvocationHandler.invoke: filter failed");
            throw new RuntimeException("invoke filter failed");
        }

        RESP_MAP.put(invocation.getUuid(), OBJECT); // placeholder
        SEND_QUEUE.add(invocation);

        // TODO refactor this, Proxy don't need to know the implementation of retry and queue
        long beginTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - beginTime < 3 * 1000) {
            Object result = RESP_MAP.get(invocation.getUuid());
            if (result instanceof Invocation) {
                return ((Invocation) result).getResponse();
            }
        }
        log.error("JDKClientInvocationHandler.invoke: timeout");
        throw new TimeoutException("rpc call timeout");
    }

    private boolean doFilter(Invocation invocation) {
        var filterChain = CommonClientCache.FILTER_LIST;

        for (ClientFilter filter : filterChain) {
            if (!filter.handle(invocation)) {
                return false;
            }
        }
        return true;
    }
}
