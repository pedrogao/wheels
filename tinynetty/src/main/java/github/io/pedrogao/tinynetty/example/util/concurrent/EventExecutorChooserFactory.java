package github.io.pedrogao.tinynetty.example.util.concurrent;

public interface EventExecutorChooserFactory {
    EventExecutorChooser newChooser(EventExecutor[] executors);

    interface EventExecutorChooser {
        EventExecutor next();
    }
}
