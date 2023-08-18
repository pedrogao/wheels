package github.io.pedrogao.tinyrpc.core.common.event;

import github.io.pedrogao.tinyrpc.core.common.event.listener.ServiceUpdateListener;
import github.io.pedrogao.tinyrpc.core.common.utils.CommonUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ListenerLoader {
    private static final List<Listener<?>> listeners = new ArrayList<>();

    private static final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public static void registryListener(Listener<?> listener) {
        listeners.add(listener);
    }

    public static Class<?> getInterfaceType(Object o) {
        Type[] types = o.getClass().getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType) types[0];
        Type type = parameterizedType.getActualTypeArguments()[0];
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        return null;
    }

    public static void sendEvent(Event event) {
        if (CommonUtils.isEmptyList(listeners)) {
            return;
        }
        for (Listener<?> listener : listeners) {
            Class<?> type = getInterfaceType(listener);
            if (type == null) {
                continue;
            }
            if (type.equals(event.getClass())) {
                executorService.execute(() -> {
                    try {
                        listener.callback(event.getData());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    public void init() {
        registryListener(new ServiceUpdateListener()); // service update listener
    }
}
