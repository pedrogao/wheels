package github.io.pedrogao.tinynetty.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class SimpleServer {
    public static void main(String[] args) throws IOException {
        final Logger log = LoggerFactory.getLogger(SimpleServer.class);

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        SelectionKey selectionKey = serverSocketChannel.register(selector, 0, serverSocketChannel); // register serverSocketChannel to selector
        selectionKey.interestOps(SelectionKey.OP_ACCEPT); // set interestOps to accept
        serverSocketChannel.bind(new InetSocketAddress(8080));

        NioEventLoop[] workerGroup = new NioEventLoop[2];
        workerGroup[0] = new NioEventLoop();
        workerGroup[1] = new NioEventLoop();
        int i = 0;

        while (true) {
            log.info("Server is running");

            selector.select(); // blocking
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = channel.accept();

                    int index = i % workerGroup.length;
                    i++;
                    workerGroup[index].register(socketChannel, workerGroup[index]);
                    log.info("{} accept client: {}", index, socketChannel.getRemoteAddress());

                    socketChannel.write(ByteBuffer.wrap("hello".getBytes()));
                }
            }
        }
    }
}
