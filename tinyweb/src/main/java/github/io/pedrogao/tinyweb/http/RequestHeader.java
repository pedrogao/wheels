package github.io.pedrogao.tinyweb.http;

import github.io.pedrogao.tinyweb.socket.ISocketWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeader {

    private static final Logger log = LoggerFactory.getLogger(RequestHeader.class);

    private final Map<String, String> headers;

    private final List<String> rawLines;

    public RequestHeader(List<String> lines) {
        this.rawLines = lines;
        this.headers = new HashMap<>();
        parseHeaders(lines);
    }

    private void parseHeaders(List<String> lines) {
        for (String line : lines) {
            int i = line.indexOf(':');
            if (i == -1) {
                log.error("Invalid header line: " + line);
                throw new IllegalArgumentException("Invalid header line: " + line);
            }
            var parts = line.split(":", 2);
            var key = parts[0].trim();
            var value = parts[1].trim();
            headers.put(key, value);
        }
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public List<String> getRawLines() {
        return rawLines;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getHeader(String key, String defaultValue) {
        return headers.getOrDefault(key, defaultValue);
    }

    public String getHost() {
        return getHeader("Host");
    }

    public int getContentLength() {
        return Integer.parseInt(getHeader("Content-Length", "-1"));
    }

    public String getContentType() {
        return getHeader("Content-Type");
    }

    public static RequestHeader parse(ISocketWrapper sw) {
        var lines = new ArrayList<String>();

        String line;
        while (true) {
            try {
                line = sw.readLine();
                if (line != null && line.isBlank()) {
                    break;
                } else if (line == null) {
                    break;
                } else {
                    lines.add(line);
                }
            } catch (IOException e) {
                log.error("Error reading request header", e);
                throw new RuntimeException(e);
            }
        }
        return new RequestHeader(lines);
    }
}
