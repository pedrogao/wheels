package github.io.pedrogao.tinynetty.example.util.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public interface EventExecutorGroup extends Executor {
    EventExecutor next();

    void shutdownGracefully();

    boolean isTerminated();

    void awaitTermination(Integer integer, TimeUnit timeUnit) throws InterruptedException;
}
