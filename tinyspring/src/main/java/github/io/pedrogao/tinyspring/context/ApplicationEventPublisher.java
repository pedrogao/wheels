package github.io.pedrogao.tinyspring.context;

public interface ApplicationEventPublisher {
    void publishEvent(ApplicationEvent event);
}
