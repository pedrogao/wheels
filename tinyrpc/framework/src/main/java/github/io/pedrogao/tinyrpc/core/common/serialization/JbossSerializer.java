package github.io.pedrogao.tinyrpc.core.common.serialization;

public class JbossSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T obj) {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) {
        return null;
    }
}
