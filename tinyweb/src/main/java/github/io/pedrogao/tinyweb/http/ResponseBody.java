package github.io.pedrogao.tinyweb.http;

public class ResponseBody {
    private byte[] body;

    public ResponseBody(byte[] body) {
        this.body = body;
    }

    public byte[] getBody() {
        return body;
    }
}
