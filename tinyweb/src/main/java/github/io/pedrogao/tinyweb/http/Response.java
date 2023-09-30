package github.io.pedrogao.tinyweb.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class Response {
    private ResponseHeader responseHeader;

    private ResponseBody responseBody;

    private HttpVersion version;

    private int statusCode;

    private String statusMessage;

    public Response(HttpVersion version, int statusCode, String statusMessage,
                    ResponseHeader responseHeader, ResponseBody responseBody) {
        this.version = version;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public void write(OutputStream os) throws IOException {
        // start line
        String startLine = String.format("%s %d %s", version.toString(), statusCode, statusMessage);
        os.write(startLine.getBytes());
        os.write(Constants.HTTP_CRLF.getBytes());
        // headers
        writeHeaders(os);
        // body
        writeBody(os);
        // flush
        os.flush();
    }

    private void writeHeaders(OutputStream os) throws IOException {
        String contentType = "Content-Type: " + responseHeader.getContentType();
        os.write(contentType.getBytes());
        os.write(Constants.HTTP_CRLF.getBytes());

        String contentLength = String.format("Content-Length: %d", responseHeader.getContentLength());
        os.write(contentLength.getBytes());
        os.write(Constants.HTTP_CRLF.getBytes());

        for (Map.Entry<String, String> entry : responseHeader.getHeaders().entrySet()) {
            os.write(entry.getKey().getBytes());
            os.write(": ".getBytes());
            os.write(entry.getValue().getBytes());
            os.write(Constants.HTTP_CRLF.getBytes());
        }
        os.write(Constants.HTTP_CRLF.getBytes());
    }

    private void writeBody(OutputStream os) throws IOException {
        os.write(responseBody.getBody());
        // os.write(Constants.HTTP_CRLF.getBytes());
        // os.write(Constants.HTTP_CRLF.getBytes());
    }

    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public ResponseBody getResponseBody() {
        return responseBody;
    }

    public HttpVersion getVersion() {
        return version;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
