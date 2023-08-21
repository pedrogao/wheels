package github.io.pedrogao.tinynetty.example;

import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SimpleClient {

    public static void main(String[] args) throws IOException {
        final Logger log = org.slf4j.LoggerFactory.getLogger(SimpleClient.class);
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        Selector selector = Selector.open();
        SelectionKey selectionKey = socketChannel.register(selector, 0);
        selectionKey.interestOps(SelectionKey.OP_CONNECT);

        socketChannel.connect(new InetSocketAddress(8080));

        // listen loop
        while (true) {
            selector.select();

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isConnectable()) {
                    if (socketChannel.finishConnect()) {
                        log.info("connect success");
                        selectionKey.interestOps(SelectionKey.OP_READ);
                        socketChannel.write(ByteBuffer.wrap("hello".getBytes()));
                    } else {
                        log.info("connect fail");
                    }
                }

                if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int len = channel.read(buffer);
                    byte[] readBytes = new byte[len];
                    buffer.flip();
                    buffer.get(readBytes);
                    log.info("read from server: {}", new String(readBytes));
                }
            }
        }
    }
}
