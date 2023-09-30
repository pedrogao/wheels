package github.io.pedrogao.tinyweb.http;

public enum HttpVersion {
    None, HTTP_1_0, HTTP_1_1;

    public static HttpVersion parse(String version) {
        if (version == null) {
            return None;
        }
        if (version.equals("HTTP/1.0")) {
            return HTTP_1_0;
        }
        if (version.equals("HTTP/1.1")) {
            return HTTP_1_1;
        }
        return None;
    }

    public String toString() {
        return switch (this) {
            case HTTP_1_0 -> "HTTP/1.0";
            case HTTP_1_1 -> "HTTP/1.1";
            default -> "";
        };
    }
}
