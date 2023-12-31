package github.io.pedrogao.tinyrpc.core.registry.zookeeper;

import github.io.pedrogao.tinyrpc.core.registry.RegistryService;
import github.io.pedrogao.tinyrpc.core.registry.URL;

import java.util.List;

import static github.io.pedrogao.tinyrpc.core.common.cache.CommonClientCache.SUBSCRIBE_SERVICE_LIST;
import static github.io.pedrogao.tinyrpc.core.common.cache.CommonServerCache.PROVIDER_URL_SET;

public abstract class AbstractRegister implements RegistryService {
    @Override
    public void register(URL url) {
        PROVIDER_URL_SET.add(url);
    }

    @Override
    public void unRegister(URL url) {
        PROVIDER_URL_SET.remove(url);
    }

    @Override
    public void subscribe(URL url) {
        SUBSCRIBE_SERVICE_LIST.add(url.getServiceName());
    }

    @Override
    public void unSubscribe(URL url) {
        SUBSCRIBE_SERVICE_LIST.remove(url.getServiceName());
    }

    public abstract void doBeforeSubscribe(URL url);

    public abstract void doAfterSubscribe(URL url);

    public abstract List<URL> getProviderList(String serviceName);

    public abstract void close();
}
