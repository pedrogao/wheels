package github.io.pedrogao.tinyrpc.core.client.connection.router;

import github.io.pedrogao.tinyrpc.core.client.connection.ConnectionWrapper;

import java.util.List;
import java.util.Random;

public class RandomRouter implements Router {
    private final Random random = new Random();

    public RandomRouter() {
    }

    @Override
    public ConnectionWrapper select(List<ConnectionWrapper> connectionWrappers) {
        int nextInt = new Random().nextInt(connectionWrappers.size());
        return connectionWrappers.get(nextInt);
    }

    @Override
    public ConnectionWrapper select(List<ConnectionWrapper> connectionWrappers, String serviceName) {
        return select(connectionWrappers);
    }
}
