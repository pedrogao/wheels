package github.io.pedrogao.tinynetty.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public abstract class SingleThreadEventLoop extends SingleThreadEventExecutor {

    private final Logger log = LoggerFactory.getLogger(SingleThreadEventLoop.class);

    public SingleThreadEventLoop() {
    }

    public void register(SocketChannel socketChannel, NioEventLoop nioEventLoop) {
        if (inEventLoop(Thread.currentThread())) {
            // register if current thread is the event loop thread
            register0(socketChannel, nioEventLoop);
        } else {
            // or else, register task in the event loop thread
            nioEventLoop.execute(() -> register0(socketChannel, nioEventLoop));
        }
    }

    private void register0(SocketChannel channel, NioEventLoop nioEventLoop) {
        try {
            channel.configureBlocking(false);
            channel.register(nioEventLoop.selector(), SelectionKey.OP_READ);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
