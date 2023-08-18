package github.io.pedrogao.tinyrpc.core.common.event;

public class UpdateEvent implements Event {
    private Object data;

    public UpdateEvent(Object data) {
        this.data = data;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public Event setData(Object data) {
        this.data = data;
        return this;
    }
}
