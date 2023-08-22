package github.io.pedrogao.tinyrpc.core.common;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

import static github.io.pedrogao.tinyrpc.core.common.constants.ProtocolConstant.MAGIC_NUMBER;

public class TinyProtocol implements Serializable {
    @Serial
    private static final long serialVersionUID = 5359096060555795690L;
    private short magicNumber = MAGIC_NUMBER;
    private int contentLength;
    private byte[] content;

    public TinyProtocol(byte[] content) {
        this.content = content;
        this.contentLength = content.length;
    }

    public short getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(short magicNumber) {
        this.magicNumber = magicNumber;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "RpcProtocol{" +
                "contentLength=" + contentLength +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
