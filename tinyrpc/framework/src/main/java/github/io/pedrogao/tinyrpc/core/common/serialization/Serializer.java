package github.io.pedrogao.tinyrpc.core.common.serialization;

import github.io.pedrogao.tinyrpc.core.common.protocol.SerializationType;

import java.util.Map;

public interface Serializer {
    Map<Short, Serializer> serializerMap = Map.of(
            SerializationType.json, new JsonSerializer(),
            SerializationType.jboss, new JbossSerializer()
    );

    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] data, Class<T> clz);

    static Serializer getSerializer(short type) {
        return serializerMap.get(type);
    }
}
