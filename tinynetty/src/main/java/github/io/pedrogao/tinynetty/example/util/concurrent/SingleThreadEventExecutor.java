package github.io.pedrogao.tinynetty.example.util.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class SingleThreadEventExecutor implements EventExecutor {
    private final Logger log = LoggerFactory.getLogger(SingleThreadEventExecutor.class);

    private static final int ST_NOT_STARTED = 1;

    private static final int ST_STARTED = 2;

    private volatile int state = ST_NOT_STARTED;

    private static final AtomicIntegerFieldUpdater<SingleThreadEventExecutor> STATE_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(SingleThreadEventExecutor.class, "state");

    protected static final int DEFAULT_MAX_PENDING_EXECUTOR_TASKS = Integer.MAX_VALUE;

    private final Queue<Runnable> taskQueue;

    private volatile Thread thread;

    private Executor executor;

    private EventExecutorGroup parent;

    private boolean addTaskWakesUp;

    private volatile boolean interrupted;

    private final RejectedExecutionHandler rejectedExecutionHandler;

    protected SingleThreadEventExecutor(EventExecutorGroup parent, Executor executor,
                                        boolean addTaskWakesUp, Queue<Runnable> taskQueue,
                                        RejectedExecutionHandler rejectedHandler) {
        this.parent = parent;
        this.addTaskWakesUp = addTaskWakesUp;
        this.executor = executor;
        this.taskQueue = Objects.requireNonNull(taskQueue, "taskQueue");
        this.rejectedExecutionHandler = Objects.requireNonNull(rejectedHandler, "rejectedHandler");
    }

    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return new LinkedBlockingQueue<>(maxPendingTasks);
    }

    protected abstract void run();

    public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }

        addTask(task);
        startThread();
    }

    private void startThread() {
        if (state == ST_NOT_STARTED) {
            if (STATE_UPDATER.compareAndSet(this, ST_NOT_STARTED, ST_STARTED)) {
                boolean success = false;
                try {
                    doStartThread();
                    success = true;
                } finally {
                    if (!success) {
                        // restore if not success
                        STATE_UPDATER.compareAndSet(this, ST_STARTED, ST_NOT_STARTED);
                    }
                }
            }
        }
    }

    private void doStartThread() {
        executor.execute(() -> {
            thread = Thread.currentThread();
            if (interrupted) {
                thread.interrupt();
            }

            // call run() which implemented by subclass
            SingleThreadEventExecutor.this.run();
            log.info("SingleThreadEventExecutor doStartThread()");
        });
    }

    protected boolean hashTasks() {
        return !taskQueue.isEmpty();
    }

    private void addTask(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }

        if (!offerTask(task)) {
            reject(task);
        }
    }

    private boolean offerTask(Runnable task) {
        return taskQueue.offer(task);
    }

    protected void runAllTasks() {
        runAllTasksFrom(taskQueue);
    }

    private void runAllTasksFrom(Queue<Runnable> taskQueue) {
        Runnable task = pollTaskFrom(taskQueue);
        if (task == null) return;

        for (; ; ) {
            // execute task safely
            safeExecute(task);
            // try to poll next task, but return if null
            task = pollTaskFrom(taskQueue);
            if (task == null) return;
        }
    }

    private void safeExecute(Runnable task) {
        try {
            task.run();
        } catch (Throwable t) {
            log.error("Execute task but raised an exception. Task: {}", task, t);
        }
    }

    private Runnable pollTaskFrom(Queue<Runnable> taskQueue) {
        return taskQueue.poll();
    }

    protected void reject(Runnable task) {
        rejectedExecutionHandler.rejected(task, this);
    }

    public Queue<Runnable> getTaskQueue() {
        return taskQueue;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return rejectedExecutionHandler;
    }

    @Override
    public boolean isEventLoop(Thread thread) {
        return thread == this.thread;
    }

    @Override
    public void shutdownGracefully() {

    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public void awaitTermination(Integer integer, TimeUnit timeUnit) throws InterruptedException {

    }
}
