package github.io.pedrogao.tinynetty.v1;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class DefaultThreadFactory implements ThreadFactory {

    private final String namePrefix;
    private final AtomicInteger threadIndex = new AtomicInteger(1);

    public DefaultThreadFactory(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(namePrefix + "-" + threadIndex.getAndIncrement());
        return thread;
    }
}
