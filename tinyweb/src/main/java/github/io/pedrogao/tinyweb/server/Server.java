package github.io.pedrogao.tinyweb.server;

import github.io.pedrogao.tinyweb.http.*;
import github.io.pedrogao.tinyweb.socket.ISocketWrapper;
import github.io.pedrogao.tinyweb.socket.SocketWrapper;
import github.io.pedrogao.tinyweb.util.ExtendedExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * The purpose here is to make it marginally easier to
 * work with a ServerSocket.
 * <p>
 * First, instantiate this class using a running serverSocket
 * Then, by running the start method, we gain access to
 * the server's socket.  This way we can easily test / control
 * the server side but also tie it in with an ExecutorService
 * for controlling lots of server threads.
 */
public final class Server implements AutoCloseable {

    public static final Logger log = LoggerFactory.getLogger(Server.class);

    private final ServerSocket serverSocket;

    private final String serverName;

    private final ExecutorService executorService;

    private final Set<ISocketWrapper> socketSet;

    private final int timeoutMillis;

    private volatile boolean stopFlag = false;

    public Server(ServerSocket ss, String serverName) {
        this(ss, serverName, 7 * 1000);
    }

    public Server(ServerSocket ss, String serverName, int timeoutMillis) {
        this.serverSocket = ss;
        this.serverName = serverName;
        this.timeoutMillis = timeoutMillis;
        this.socketSet = new CopyOnWriteArraySet<>();
        this.executorService = ExtendedExecutor.makeExecutorService();
    }

    /**
     * This is the infinite loop running inside the basic socket server code.  Every time we
     * {@link ServerSocket#accept()} a new incoming socket connection, we attach our "end
     * of the phone line" to the code that is "handler", which mainly handles it from there.
     */
    public Future<?> start() {
        log.info("Starting server: " + this);
        return executorService.submit(this::mainServerLoop);
    }

    /**
     * This code is the innermost loop of the server, waiting for incoming
     * connections and then delegating their handling off to a handler.
     */
    private void mainServerLoop() {
        Thread.currentThread().setName("main-server-loop");
        try {
            while (!stopFlag) {
                Socket freshSocket = serverSocket.accept();
                ISocketWrapper sw = new SocketWrapper(freshSocket, timeoutMillis);
                socketSet.add(sw);
                executorService.submit(() -> handle(sw));
            }
        } catch (IOException ex) {
            log.error("Error in main server loop", ex);
        }
    }

    private void handle(ISocketWrapper sw) {
        log.info(Thread.currentThread().getName() + " handling socket: " + sw);
        try {
            parse(sw);
        } catch (Exception ex) {
            log.error("Error handling socket: " + sw, ex);
        }
    }

    private void parse(ISocketWrapper sw) {
        try {
            final var startLine = StartLine.parse(sw);
            final var requestHeader = RequestHeader.parse(sw);
            final var requestBody = RequestBody.parse(sw, requestHeader);
            log.info("startLine: " + startLine);
            log.info("requestHeader: " + requestHeader);
            log.info("requestBody: " + requestBody);
            ResponseBody responseBody = new ResponseBody("Hello World!".getBytes());
            ResponseHeader responseHeader = new ResponseHeader("text/plain", responseBody.getBody().length);
            Response response = new Response(HttpVersion.HTTP_1_1, 200, "OK", responseHeader, responseBody);
            log.info("response: " + response);
            response.write(sw.getWriter());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() throws IOException {
        if (!stopFlag) {
            stopFlag = true;
        }
        // close all the sockets
        for (ISocketWrapper sw : socketSet) {
            sw.close();
        }
        // close the primary server socket
        serverSocket.close();
    }

    /**
     * Get the string version of the address of this
     * server.  See {@link InetAddress#getHostAddress()}
     */
    String getHost() {
        return serverSocket.getInetAddress().getHostAddress();
    }

    /**
     * See {@link ServerSocket#getLocalPort()}
     */
    int getPort() {
        return serverSocket.getLocalPort();
    }

    /**
     * Returns the name of this server, which is set
     * when the server is instantiated.
     */
    @Override
    public String toString() {
        return this.serverName;
    }

}
