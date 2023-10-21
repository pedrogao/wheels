package github.io.pedrogao.patterns.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


/**
 * Handy helpful utilities for working with input streams.
 */
public final class InputStreamUtils {

    public static final Logger log = LoggerFactory.getLogger(InputStreamUtils.class);

    /**
     * Read from the socket until it returns an EOF indicator (that is, -1)
     * Note: this *will block* until it gets to that EOF.
     */
    public static byte[] readUntilEOF(InputStream inputStream) throws IOException {
        final var result = new ArrayList<Byte>();
        for (int i = 0; i <= (Constants.MAX_READ_SIZE_BYTES + 1); i++) {
            if (i == Constants.MAX_READ_SIZE_BYTES)
                throw new IOException("client sent more bytes than allowed.  Current max: " + Constants.MAX_READ_SIZE_BYTES);
            int a = inputStream.read();
            if (a == -1) {
                return byteListToArray(result);
            }

            result.add((byte) a);
        }
        return byteListToArray(result);
    }

    public static boolean atEOF(InputStream inputStream) throws IOException {
        inputStream.mark(1);
        int end = inputStream.read();
        return end == -1;
    }

    private static byte[] byteListToArray(ArrayList<Byte> result) {
        byte[] bytes = new byte[result.size()];
        for (int i = 0; i < result.size(); i++) {
            bytes[i] = result.get(i);
        }
        return bytes;
    }

    /**
     * reads following the algorithm for transfer-encoding: chunked.
     * See <a href="https://en.wikipedia.org/wiki/Chunked_transfer_encoding">chunked transfer encoding</a>
     */
    public static byte[] readChunkedEncoding(InputStream inputStream) throws IOException {
        final var result = new ByteArrayOutputStream();
        for (int countRead = 0; countRead <= (Constants.MAX_READ_SIZE_BYTES + 1); ) {
            if (countRead == Constants.MAX_READ_SIZE_BYTES) {
                log.debug("client sent more bytes than allowed.  Current max: " + Constants.MAX_READ_SIZE_BYTES);
                inputStream.close();
                return new byte[0];
            }
            String countToReadString = readLine(inputStream);
            if (countToReadString == null) {
                return new byte[0];
            }
            assert !countToReadString.isEmpty();

            int countToRead = Integer.parseInt(countToReadString, 16);

            result.write(read(countToRead, inputStream));
            countRead += countToRead;

            readLine(inputStream);
            if (countToRead == 0) {
                readLine(inputStream);
                break;
            }

        }
        return result.toByteArray();
    }

    /**
     * Reads a line of text, stopping when reading a newline.
     * Skips over carriage returns, so we read an HTTP_CRLF properly.
     * <br>
     * If the stream ends, return null
     */
    public static String readLine(InputStream inputStream) throws IOException {
        final int NEWLINE_DECIMAL = 10;
        final int CARRIAGE_RETURN_DECIMAL = 13;

        final var result = new ByteArrayOutputStream(Constants.MAX_READ_LINE_SIZE_BYTES / 3);
        for (int i = 0; i <= (Constants.MAX_READ_LINE_SIZE_BYTES + 1); i++) {
            if (i == Constants.MAX_READ_LINE_SIZE_BYTES) {
                log.debug("in readLine, client sent more bytes than allowed.  Current max: " + Constants.MAX_READ_LINE_SIZE_BYTES);
                inputStream.close();
                return "";
            }
            int a = inputStream.read();

            if (a == -1) return null;

            if (a == CARRIAGE_RETURN_DECIMAL) {
                continue;
            }

            if (a == NEWLINE_DECIMAL) break;

            result.write(a);

        }
        return result.toString(StandardCharsets.UTF_8);
    }


    /**
     * Reads "lengthToRead" bytes from the input stream
     */
    public static byte[] read(int lengthToRead, InputStream inputStream) throws IOException {
        if (lengthToRead > Constants.MAX_READ_SIZE_BYTES) {
            throw new IOException("client requested to send more bytes than allowed.  Current max: " +
                    Constants.MAX_READ_SIZE_BYTES + " asked to receive: " + lengthToRead);
        }
        final int typicalBufferSize = 1024 * 8;
        byte[] buf = new byte[Math.min(lengthToRead, typicalBufferSize)];
        byte[] data;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int read;
        int totalRead = 0;
        while ((read = inputStream.read(buf)) >= 0) {
            totalRead += read;
            if (totalRead < lengthToRead) {
                // if we haven't gotten everything we wanted, write this to the output and loop again
                baos.write(buf, 0, read);
            } else {
                baos.write(buf, 0, read - (totalRead - lengthToRead));
                break;
            }
        }
        data = baos.toByteArray();

        assert data.length == lengthToRead;  // String.format("lengthToRead of bytes read (%d) must be what we expected (%d)", data.length, lengthToRead));
        return data;
    }
}
