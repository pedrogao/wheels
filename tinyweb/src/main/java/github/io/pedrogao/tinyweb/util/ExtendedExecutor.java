package github.io.pedrogao.tinyweb.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * This lets us capture the stack traces thrown in a thread,
 * which the typical Executor does not.
 */
public final class ExtendedExecutor extends ThreadPoolExecutor {

    public static final Logger log = LoggerFactory.getLogger(ExtendedExecutor.class);

    public ExtendedExecutor(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t == null
                && r instanceof Future<?>
                && ((Future<?>) r).isDone()) {
            try {
                ((Future<?>) r).get();
            } catch (CancellationException ce) {
                t = ce;
            } catch (ExecutionException ee) {
                t = ee.getCause();
            } catch (InterruptedException ie) {
                // ignore/reset
                Thread.currentThread().interrupt();
            }
        }
        if (t != null)
            log.error("after execute error ", t);
    }

    public static ExecutorService makeExecutorService() {
        Runtime.Version runtimeVersion = Runtime.version();
        String version = String.valueOf(runtimeVersion.version().get(0));
        boolean useVirtualThreads = version.equals("21");
        if (useVirtualThreads) {
            // the following line is only usable with the virtual threads API, which
            // is available on OpenJDK 19/20 in preview mode.
            log.info("use virtual threads? " + useVirtualThreads);
            return Executors.newVirtualThreadPerTaskExecutor();
        } else {
            return new ExtendedExecutor(0, Integer.MAX_VALUE,
                    60L, TimeUnit.SECONDS,
                    new SynchronousQueue<>(),
                    Executors.defaultThreadFactory());
        }
    }

}
