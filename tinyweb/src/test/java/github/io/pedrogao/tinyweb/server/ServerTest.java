package github.io.pedrogao.tinyweb.server;

import junit.framework.TestCase;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ServerTest extends TestCase {

    public void testStart() throws IOException, ExecutionException, InterruptedException {
        final var socketServer = new ServerSocket(3000);
        final var server = new Server(socketServer, "testServer");
        Future<?> future = server.start();
        Object o = future.get();
        System.out.println(o);
    }
}