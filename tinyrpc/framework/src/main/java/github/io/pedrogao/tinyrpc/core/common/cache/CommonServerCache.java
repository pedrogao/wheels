package github.io.pedrogao.tinyrpc.core.common.cache;

import github.io.pedrogao.tinyrpc.core.registry.URL;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommonServerCache {

    // service provider implement class
    public static final Map<String, Object> PROVIDER_CLASS_MAP = new ConcurrentHashMap<>();

    // service provider registry url set
    public static final List<URL> PROVIDER_URL_SET = new CopyOnWriteArrayList<>();
}
