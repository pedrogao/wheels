package github.io.pedrogao.tinyrpc.core.proxy.jdk;

import github.io.pedrogao.tinyrpc.core.common.RpcInvocation;
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
        RpcInvocation invocation = new RpcInvocation();
        invocation.setTargetServiceName(clazz.getName());
        invocation.setTargetMethod(method.getName());
        invocation.setArgs(args);
        invocation.setUuid(UUID.randomUUID().toString());

        RESP_MAP.put(invocation.getUuid(), OBJECT);
        SEND_QUEUE.add(invocation);

        long beginTime = System.currentTimeMillis();

        // wait for rpc response
        // timeout: 3s
        while (System.currentTimeMillis() - beginTime < 3 * 1000) {
            Object result = RESP_MAP.get(invocation.getUuid());
            if (result instanceof RpcInvocation) {
                return ((RpcInvocation) result).getResponse();
            }
        }
        log.error("JDKClientInvocationHandler.invoke: timeout");
        throw new TimeoutException("rpc call timeout");
    }
}
