package github.io.pedrogao.tinynetty.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class SingleThreadEventExecutor implements Executor {

    private final Logger log = LoggerFactory.getLogger(SingleThreadEventExecutor.class);

    protected static final int DEFAULT_MAX_PENDING_TASKS = Integer.MAX_VALUE;

    private final Queue<Runnable> taskQueue;

    private final RejectedExecutionHandler rejectedExecutionHandler;

    private volatile boolean start = false;

    private Thread thread;

    public SingleThreadEventExecutor() {
        taskQueue = newTaskQueue(DEFAULT_MAX_PENDING_TASKS);
        rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
    }

    private Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return new LinkedBlockingDeque<>(maxPendingTasks);
    }

    @Override
    public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }

        addTask(task);
        startThread();
    }

    private void startThread() {
        if (start) return;

        start = true;

        new Thread(() -> {
            thread = Thread.currentThread();
            SingleThreadEventExecutor.this.run();
        }).start();

        log.info("new thread started");
    }

    protected abstract void run();

    private void addTask(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }

        if (!offerTask(task)) {
            reject(task);
        }
    }

    protected final void reject(Runnable task) {
        // TODO
    }

    private boolean offerTask(Runnable task) {
        return taskQueue.offer(task);
    }

    protected boolean hasTasks() {
        return !taskQueue.isEmpty();
    }

    protected void runAllTasks() {
        runAllTasksFrom(taskQueue);
    }

    private void runAllTasksFrom(Queue<Runnable> taskQueue) {
        Runnable task = pollTaskFrom(taskQueue);
        if (task == null) {
            return;
        }

        for (; ; ) {
            safeExecute(task);

            task = pollTaskFrom(taskQueue);
            if (task == null) break;
        }
    }

    private void safeExecute(Runnable task) {
        try {
            task.run();
        } catch (Throwable t) {
            log.error("Error while executing task", t);
        }
    }

    private Runnable pollTaskFrom(Queue<Runnable> taskQueue) {
        return taskQueue.poll();
    }

    public boolean inEventLoop(Thread thread) {
        return thread == this.thread;
    }
}
