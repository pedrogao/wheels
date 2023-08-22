package github.io.pedrogao.tinyspring.context;

import java.util.EventListener;

public class ApplicationListener implements EventListener {
    void onApplicationEvent(ApplicationEvent event) {
        System.out.println("ApplicationListener: " + event.msg);
    }
}
