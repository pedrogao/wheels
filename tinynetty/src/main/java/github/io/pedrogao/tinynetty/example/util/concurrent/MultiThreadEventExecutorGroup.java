package github.io.pedrogao.tinynetty.example.util.concurrent;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public abstract class MultiThreadEventExecutorGroup extends AbstractEventExecutorGroup {

    private final EventExecutor[] children;

    private final Set<EventExecutor> readonlyChildren;

    private final EventExecutorChooserFactory.EventExecutorChooser eventExecutorChooser;

    public MultiThreadEventExecutorGroup(int nThreads, Executor executor,
                                         EventExecutorChooserFactory chooserFactory, Object... args) {
        if (nThreads <= 0) {
            throw new IllegalArgumentException("nThreads: " + nThreads + " (expected: > 0)");
        }
        if (executor == null) {
            executor = new ThreadPerTaskExecutor(newDefaultThreadFactory());
        }
        children = new EventExecutor[nThreads];

        for (int i = 0; i < nThreads; i++) {
            boolean success = false;
            try {
                children[i] = newChild(executor, args);
                success = true;
            } catch (Exception e) {
                throw new IllegalStateException("failed to create a child event loop", e);
            } finally {
                if (!success) {
                    for (int j = 0; j < i; j++) {
                        children[j].shutdownGracefully();
                    }

                    for (int j = 0; j < i; j++) {
                        EventExecutor e = children[j];
                        try {
                            while (!e.isTerminated()) {
                                e.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                            }
                        } catch (InterruptedException interrupted) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }
        }

        eventExecutorChooser = chooserFactory.newChooser(children);
        Set<EventExecutor> childrenSet = new LinkedHashSet<>(children.length);
        Collections.addAll(childrenSet, children);
        readonlyChildren = Collections.unmodifiableSet(childrenSet);
    }


    @Override
    public EventExecutor next() {
        return eventExecutorChooser.next();
    }

    @Override
    public void shutdownGracefully() {
        for (EventExecutor l : children) {
            l.shutdownGracefully();
        }
    }

    protected ThreadFactory newDefaultThreadFactory() {
        return new DefaultThreadFactory(getClass());
    }

    public final int executorCount() {
        return children.length;
    }

    protected abstract EventExecutor newChild(Executor executor, Object... args) throws Exception;

}
