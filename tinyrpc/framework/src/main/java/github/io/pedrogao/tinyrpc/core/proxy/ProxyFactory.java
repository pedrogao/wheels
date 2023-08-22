package github.io.pedrogao.tinyrpc.core.proxy;

import github.io.pedrogao.tinyrpc.core.proxy.javassist.JavassistProxyFactory;
import github.io.pedrogao.tinyrpc.core.proxy.jdk.JDKProxyFactory;

public interface ProxyFactory {
    ProxyFactory DEFAULT_PROXY_TYPE = newJDKProxyFactory();

    <T> T getProxy(final Class<?> clazz) throws Throwable;

    static ProxyFactory newJavassistProxyFactory() {
        return new JavassistProxyFactory();
    }

    static ProxyFactory newJDKProxyFactory() {
        return new JDKProxyFactory();
    }

    static <T> T get(Class<T> clazz) throws Throwable {
        return DEFAULT_PROXY_TYPE.getProxy(clazz);
    }
}
