package github.io.pedrogao.patterns.socket;

import github.io.pedrogao.patterns.util.InputStreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.Charset;


/**
 * This wraps Sockets to make them more particular to our use case
 */
public final class SocketWrapper implements ISocketWrapper, AutoCloseable {

    private final Socket socket;
    private final InputStream reader;
    private final OutputStream writer;

    /**
     * Constructor
     *
     * @param socket        a socket we intend to wrap with methods applicable to our use cases
     * @param timeoutMillis we'll configure the socket to timeout after this many milliseconds.
     */
    public SocketWrapper(Socket socket, int timeoutMillis) throws IOException {
        this.socket = socket;
        this.socket.setSoTimeout(timeoutMillis);
        this.reader = socket.getInputStream();
        this.writer = socket.getOutputStream();
    }

    @Override
    public void write(String msg) throws IOException {
        writer.write(msg.getBytes(Charset.defaultCharset()));
    }

    @Override
    public void write(byte[] contents) throws IOException {
        writer.write(contents);
    }

    @Override
    public byte[] read(int length) throws IOException {
        return InputStreamUtils.read(length, reader);
    }

    @Override
    public byte[] readUntilEOF() throws IOException {
        return InputStreamUtils.readUntilEOF(reader);
    }

    @Override
    public String readLine() throws IOException {
        return InputStreamUtils.readLine(reader);
    }

    @Override
    public String getLocalAddr() {
        return socket.getLocalAddress().getHostAddress();
    }

    @Override
    public int getLocalPort() {
        return socket.getLocalPort();
    }

    @Override
    public SocketAddress getRemoteAddrWithPort() {
        return socket.getRemoteSocketAddress();
    }

    @Override
    public String getRemoteAddr() {
        return socket.getInetAddress().getHostAddress();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public InputStream getReader() {
        return this.reader;
    }

    @Override
    public OutputStream getWriter() {
        return this.writer;
    }

    /**
     * Note that since we are indicating just the remote address
     * as the unique value, in cases like tests where we are operating as
     * sometimes server or client, you might see the server as the remote.
     */
    @Override
    public String toString() {
        return "(SocketWrapper for remote address: " + this.getRemoteAddrWithPort().toString() + ")";
    }
}
