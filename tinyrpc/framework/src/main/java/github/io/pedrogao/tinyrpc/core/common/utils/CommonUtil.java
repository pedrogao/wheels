package github.io.pedrogao.tinyrpc.core.common.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class CommonUtil {
    public static boolean isEmptyList(List<?> list) {
        return list == null || list.isEmpty();
    }

    public static String getIpAddress() {
        // get current ip address
        InetAddress ip = null;
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            return "";
        }
        return ip.getHostAddress();
    }
}
