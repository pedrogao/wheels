package github.io.pedrogao.tinyrpc.core.common.event;

public interface Listener<T> {
    void callback(Object event);
}
