package github.io.pedrogao.tinynetty.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

public class Work implements Runnable {
    private final Logger log = LoggerFactory.getLogger(Work.class);

    private volatile boolean start;

    private final SelectorProvider provider;

    private final Selector selector;

    private final Thread thread;

    private SelectionKey selectionKey;

    protected SocketChannel socketChannel;

    public Work() throws IOException {
        provider = SelectorProvider.provider();
        selector = openSelector();
        thread = new Thread(this);
    }

    public void register(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        try {
            socketChannel.configureBlocking(false);
            selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
            start();
        } catch (IOException e) {
            log.error("Error while registering socketChannel", e);
            throw new RuntimeException(e);
        }

    }

    private Selector openSelector() {
        try {
            return provider.openSelector();
        } catch (IOException e) {
            log.error("Error while opening selector", e);
            throw new RuntimeException(e);
        }
    }

    public Selector getSelector() {
        return selector;
    }

    public void start() {
        if (start)
            return;

        start = true;
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            log.info("Work is running");

            try {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isReadable()) {
                        log.info("Readable");

                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int len = channel.read(buffer);
                        if (len == -1) {
                            log.info("Connection closed");
                            channel.close();
                            break;
                        }

                        log.info("Read {} bytes", len);
                        byte[] bytes = new byte[len];
                        buffer.flip();
                        buffer.get(bytes);
                        log.info("Read {}", new String(bytes));
                    }

                }
            } catch (IOException e) {
                log.error("Error while selecting", e);
            }
        }
    }
}
