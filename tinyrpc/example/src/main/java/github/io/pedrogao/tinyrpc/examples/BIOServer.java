package github.io.pedrogao.tinyrpc.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOServer {
    private static final ExecutorService executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static void main(String[] args) throws IOException {
        final Logger log = LoggerFactory.getLogger(BIOServer.class);

        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(1009));

        try(ServerSocket ignored = serverSocket) {
            while (true) {
                Socket socket = serverSocket.accept();
                log.info("Accept connection from {}", socket.getRemoteSocketAddress());

                executors.execute(() -> {
                    while (true) {
                        InputStream inputStream;
                        try {
                            inputStream = socket.getInputStream();
                            byte[] bytes = new byte[1024];
                            int len;
                            while ((len = inputStream.read(bytes)) != -1) {
                                log.info("Receive message: {}", new String(bytes, 0, len));
                            }
                        } catch (IOException e) {
                            log.error("Read error", e);
                            break;
                        }
                    }
                });
            }
        } catch (Exception e) {
            log.error("BIO Server error", e);
        }
    }
}
