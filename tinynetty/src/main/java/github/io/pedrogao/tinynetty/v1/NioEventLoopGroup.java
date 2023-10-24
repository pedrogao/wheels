package github.io.pedrogao.tinynetty.v1;

import java.util.Arrays;
import java.util.concurrent.ThreadFactory;

// 扩展NioEventLoopGroup
public class NioEventLoopGroup implements EventLoopGroup {
    private EventLoop[] executors;
    private ThreadFactory threadFactory;

    public NioEventLoopGroup(int nThreads) {
        threadFactory = new DefaultThreadFactory("nio");
        executors = new DefaultEventLoop[nThreads];
        for (int i = 0; i < nThreads; i++) {
            executors[i] = new DefaultEventLoop(threadFactory);
        }
    }

    @Override
    public EventLoop next() {
        // 简单轮询获取下一个执行器
        return executors[(int) (System.currentTimeMillis() % executors.length)];
    }

    @Override
    public java.util.Iterator<EventLoop> iterator() {
        return Arrays.asList(executors).iterator();
    }

    @Override
    public void execute(Runnable command) {
        // 简单轮询获取下一个执行器
        next().execute(command);
    }
}