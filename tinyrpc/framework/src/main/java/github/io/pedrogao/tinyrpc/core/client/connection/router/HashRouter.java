package github.io.pedrogao.tinyrpc.core.client.connection.router;

import github.io.pedrogao.tinyrpc.core.client.connection.ConnectionWrapper;

import java.util.List;

public class HashRouter implements Router {

    public HashRouter() {
    }

    @Override
    public ConnectionWrapper select(List<ConnectionWrapper> connectionWrappers) {
        return select(connectionWrappers, "");
    }

    @Override
    public ConnectionWrapper select(List<ConnectionWrapper> connectionWrappers, String serviceName) {
        int hashCode = serviceName.hashCode();
        int i = hashCode % connectionWrappers.size();
        return connectionWrappers.get(i);
    }
}
