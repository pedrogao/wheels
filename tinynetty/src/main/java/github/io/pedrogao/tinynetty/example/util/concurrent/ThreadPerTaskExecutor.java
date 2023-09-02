package github.io.pedrogao.tinynetty.example.util.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class ThreadPerTaskExecutor implements Executor {

    private final Logger log = LoggerFactory.getLogger(ThreadPerTaskExecutor.class);

    private final ThreadFactory threadFactory;

    public ThreadPerTaskExecutor(ThreadFactory threadFactory) {
        if (threadFactory == null) {
            throw new IllegalArgumentException("threadFactory cannot be null.");
        }

        this.threadFactory = threadFactory;
    }


    @Override
    public void execute(Runnable command) {
        threadFactory.newThread(command).start();
    }
}
