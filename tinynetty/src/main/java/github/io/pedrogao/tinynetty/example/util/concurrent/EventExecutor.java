package github.io.pedrogao.tinynetty.example.util.concurrent;

public interface EventExecutor extends EventExecutorGroup {
    @Override
    EventExecutor next();

    EventExecutorGroup parent();

    boolean isEventLoop(Thread thread);
}
