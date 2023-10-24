package github.io.pedrogao.tinynetty.v1;

import java.util.concurrent.Executor;

public interface EventLoopGroup extends Executor {

    EventLoop next();

    java.util.Iterator<EventLoop> iterator();
}