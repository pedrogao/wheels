package github.io.pedrogao.tinyweb.http;

import java.util.HashMap;
import java.util.Map;

public class ResponseHeader {
    private String contentType;

    private int contentLength;

    private Map<String, String> headers;

    public ResponseHeader(String contentType, int contentLength) {
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.headers = new HashMap<>();
    }

    public String getContentType() {
        return contentType;
    }

    public int getContentLength() {
        return contentLength;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }


}
