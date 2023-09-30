package github.io.pedrogao.tinyweb.http;

import github.io.pedrogao.tinyweb.socket.ISocketWrapper;
import github.io.pedrogao.tinyweb.util.InputStreamUtils;

import java.io.IOException;

public record RequestBody(byte[] rawBytes, String contentType) {

    public String getBody() {
        return new String(rawBytes);
    }

    public static RequestBody parse(ISocketWrapper sw, RequestHeader requestHeader) {
        int contentLength = requestHeader.getContentLength();
        String contentType = requestHeader.getContentType();

        try {
            if (contentLength == -1) {
                return new RequestBody(new byte[0], contentType);
            }
            byte[] rawBytes = contentLength > 0 ?
                    sw.read(contentLength) :
                    InputStreamUtils.readChunkedEncoding(sw.getReader());
            return new RequestBody(rawBytes, contentType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
