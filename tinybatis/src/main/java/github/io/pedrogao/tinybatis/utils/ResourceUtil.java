package github.io.pedrogao.tinybatis.utils;

import java.io.InputStream;

public class ResourceUtil {
    public static InputStream getResourceAsStream(String resource) {
        return ResourceUtil.class.getClassLoader().getResourceAsStream(resource);
    }
}
