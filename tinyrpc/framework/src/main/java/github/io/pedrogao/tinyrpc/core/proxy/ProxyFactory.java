package github.io.pedrogao.tinyrpc.core.proxy;

public interface ProxyFactory {
    <T> T getProxy(final Class<?> clazz) throws Throwable;
}
