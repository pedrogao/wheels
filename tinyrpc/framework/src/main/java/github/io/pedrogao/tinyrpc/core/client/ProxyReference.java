package github.io.pedrogao.tinyrpc.core.client;

import github.io.pedrogao.tinyrpc.core.proxy.ProxyFactory;

public class ProxyReference {

    private final ProxyFactory proxyFactory;

    public ProxyReference(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    public <T> T get(Class<T> clazz) throws Throwable {
        return proxyFactory.getProxy(clazz);
    }
}
