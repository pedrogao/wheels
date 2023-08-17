package github.io.pedrogao.tinyrpc.core.client;

import github.io.pedrogao.tinyrpc.core.proxy.ProxyFactory;

public class RpcReference {

    private final ProxyFactory proxyFactory;

    public RpcReference(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    public <T> T get(Class<T> clazz) throws Throwable {
        return proxyFactory.getProxy(clazz);
    }
}
