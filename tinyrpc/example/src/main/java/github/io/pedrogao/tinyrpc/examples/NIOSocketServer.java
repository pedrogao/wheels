package github.io.pedrogao.tinyrpc.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NIOSocketServer extends Thread {
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private SelectionKey selectionKey;

    private final Logger log = LoggerFactory.getLogger(NIOSocketServer.class);

    public void initServer() throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        // set non-blocking
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(1009));
        selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        while (true) {
            try {
                int selectKey = selector.select();
                if (selectKey == 0) {
                    continue;
                }

                Set<SelectionKey> keySet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keySet.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        accept(key);
                    }
                    if (key.isReadable()) {
                        read(key);
                    }
                    if (key.isWritable()) {
                        // do something
                    }
                    iterator.remove();
                }
            } catch (IOException e) {
                log.error("NIOSocketServer error", e);
                try {
                    serverSocketChannel.close();
                } catch (IOException ex) {
                    log.error("Close serverSocketChannel error", ex);
                }
            }
        }
    }

    private void read(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        try {
            int len = channel.read(byteBuffer);
            if (len > 0) {
                byteBuffer.flip();
                byte[] bytes = new byte[byteBuffer.limit()];
                byteBuffer.get(bytes);
                log.info("Receive message: {}", new String(bytes));
                key.interestOps(SelectionKey.OP_READ);
            }
        } catch (IOException e) {
            log.error("Read error", e);
            try {
                channel.close();
            } catch (IOException ex) {
                log.error("Close channel error", ex);
            }
        }
    }

    private void accept(SelectionKey key) {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        try {
            SocketChannel socketChannel = serverSocketChannel.accept();
            log.info("Accept connection from {}", socketChannel.getRemoteAddress());
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            log.error("Accept error", e);
        }
    }

    public static void main(String[] args) throws IOException {
        NIOSocketServer server = new NIOSocketServer();
        server.initServer();
        server.start();
    }
}
