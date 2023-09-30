package github.io.pedrogao.tinyweb.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketAddress;

/**
 * This is the public interface to {@link ISocketWrapper}, whose
 * purpose is to make our lives easier when working with {@link java.net.Socket}.
 */
public interface ISocketWrapper extends AutoCloseable {

    /**
     * Convert the provided string value into bytes
     * using the default charset, and send on the socket.
     */
    void write(String msg) throws IOException;

    /**
     * Simply send the bytes on the socket, simple as that.
     */
    void write(byte[] contents) throws IOException;

    /**
     * Read the specified number of bytes from the socket.
     * Note: this *will block* until it gets to that EOF.
     */
    byte[] read(int length) throws IOException;

    /**
     * Read from the socket until it returns an EOF indicator (that is, -1)
     * Note: this *will block* until it gets to that EOF.
     */
    byte[] readUntilEOF() throws IOException;

    /**
     * Read from the socket until it returns a newline character
     * Note: this *will block* until it gets to that EOF.
     */
    String readLine() throws IOException;

    /**
     * A live running socket connects a local address and port to a
     * remote address and port. This returns the local address.
     */
    String getLocalAddr();

    /**
     * Get the port of the server
     */
    int getLocalPort();

    /**
     * Returns a {@link SocketAddress}, which includes
     * the client's address and port
     */
    SocketAddress getRemoteAddrWithPort();

    /**
     * Returns a string of the remote host address without port
     */
    String getRemoteAddr();

    void close() throws IOException;

    /**
     * Returns this socket's input stream for more granular access
     */
    InputStream getReader();

    OutputStream getWriter();
}
