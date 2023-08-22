package github.io.pedrogao.tinyrpc.core.common.serialization;

import com.alibaba.fastjson.JSON;

public class JsonSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T obj) {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) {
        return JSON.parseObject(data, clz);
    }
}
