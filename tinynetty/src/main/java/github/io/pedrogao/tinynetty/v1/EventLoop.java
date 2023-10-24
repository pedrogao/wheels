package github.io.pedrogao.tinynetty.v1;

import java.util.concurrent.Executor;

public interface EventLoop extends Executor {
    void execute(Runnable command);
}
