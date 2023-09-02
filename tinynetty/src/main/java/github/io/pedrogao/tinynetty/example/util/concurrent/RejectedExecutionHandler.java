package github.io.pedrogao.tinynetty.example.util.concurrent;

public interface RejectedExecutionHandler {
    void rejected(Runnable task, SingleThreadEventExecutor executor);
}
