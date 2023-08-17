package github.io.pedrogao.tinyrpc.core.common.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommonServerCache {
    public static final Map<String, Object> PROVIDER_CLASS_MAP = new ConcurrentHashMap<>();
}
