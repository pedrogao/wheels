package github.io.pedrogao.tinyrpc.core.common.protocol;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

import static github.io.pedrogao.tinyrpc.core.common.constants.ProtocolConstant.DEFAULT_VERSION;
import static github.io.pedrogao.tinyrpc.core.common.constants.ProtocolConstant.MAGIC_NUMBER;

public class TinyProtocol implements Serializable {
    @Serial
    private static final long serialVersionUID = 5359096060555795690L;
    private short magicNumber = MAGIC_NUMBER;
    private short version = DEFAULT_VERSION;
    private short serialization = SerializationType.json;
    private int contentLength;
    private byte[] content;

    public TinyProtocol(byte[] content) {
        this.content = content;
        this.contentLength = content.length;
    }

    public TinyProtocol(short version, short serialization, byte[] content) {
        this.version = version;
        this.serialization = serialization;
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

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public short getSerialization() {
        return serialization;
    }

    public void setSerialization(short serialization) {
        this.serialization = serialization;
    }

    @Override
    public String toString() {
        return "TinyProtocol{" +
                "magicNumber=" + magicNumber +
                ", version=" + version +
                ", serialization=" + serialization +
                ", contentLength=" + contentLength +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
