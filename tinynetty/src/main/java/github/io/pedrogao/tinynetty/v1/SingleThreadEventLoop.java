package github.io.pedrogao.tinynetty.v1;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class SingleThreadEventLoop implements EventLoop {
    private final Queue<Runnable> taskQueue = new ArrayBlockingQueue<>(100);

    @Override
    public void execute(Runnable command) {
        taskQueue.offer(command);
    }

    public void run() {
        while (true) {
            Runnable task = taskQueue.poll();
            if (task != null) {
                task.run();
            }
        }
    }
}