package github.io.pedrogao.tinyrpc.core.client.connection.router;

import github.io.pedrogao.tinyrpc.core.client.connection.ConnectionWrapper;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class GrayRouter implements Router {
    private final Set<String> graySet = new CopyOnWriteArraySet<>();

    public GrayRouter() {
    }

    public GrayRouter(String host, int port) {
        addRule(host, port);
    }

    public void addRule(String host, int port) {
        String address = formatAddress(host, port);
        graySet.add(address);
    }

    public void removeRule(String host, int port) {
        String address = formatAddress(host, port);
        graySet.remove(address);
    }

    @Override
    public ConnectionWrapper select(List<ConnectionWrapper> connectionWrappers) {
        for (ConnectionWrapper connectionWrapper : connectionWrappers) {
            String address = formatAddress(connectionWrapper.getHost(), connectionWrapper.getPort());
            if (graySet.contains(address)) {
                return connectionWrapper;
            }
        }

        throw new IllegalArgumentException("no available router");
    }

    @Override
    public ConnectionWrapper select(List<ConnectionWrapper> connectionWrappers, String serviceName) {
        return select(connectionWrappers);
    }

    private String formatAddress(String host, int port) {
        return host + ":" + port;
    }
}
