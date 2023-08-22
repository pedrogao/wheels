package github.io.pedrogao.tinyrpc.core.common.protocol;

public interface SerializationType {
    short unknown = 0;

    short json = 1;

    short jboss = 2;

    short protobuf = 2;

    // TODO: add more serialization types
}
