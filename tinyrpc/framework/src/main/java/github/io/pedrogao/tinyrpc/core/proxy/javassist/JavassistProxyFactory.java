package github.io.pedrogao.tinyrpc.core.proxy.javassist;

import github.io.pedrogao.tinyrpc.core.proxy.ProxyFactory;

public class JavassistProxyFactory implements ProxyFactory {
    @Override
    public <T> T getProxy(Class<?> clazz) throws Throwable {
        return (T) ProxyGenerator.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                clazz, new JavassistInvocationHandler(clazz));
    }
}
