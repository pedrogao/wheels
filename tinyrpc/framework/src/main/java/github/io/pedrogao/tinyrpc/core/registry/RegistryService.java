package github.io.pedrogao.tinyrpc.core.registry;

/**
 * Registry Service interface
 */
public interface RegistryService {
    void register(URL url);

    void unRegister(URL url);

    void subscribe(URL url);

    void unSubscribe(URL url);
}
