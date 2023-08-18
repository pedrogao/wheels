package github.io.pedrogao.tinyrpc.core.common.event;

public interface Event {
    Object getData();

    Event setData(Object data);
}
