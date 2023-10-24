package github.io.pedrogao.tinynetty.v1;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class DefaultEventLoop implements EventLoop {
    private final Executor executor;

    public DefaultEventLoop(ThreadFactory threadFactory) {
        this.executor = Executors.newSingleThreadExecutor(threadFactory);
    }

    @Override
    public void execute(Runnable command) {
        executor.execute(command);
    }
}
