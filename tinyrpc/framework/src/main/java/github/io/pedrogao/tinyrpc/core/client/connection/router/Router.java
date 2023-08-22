package github.io.pedrogao.tinyrpc.core.client.connection.router;

import github.io.pedrogao.tinyrpc.core.client.connection.ConnectionWrapper;

import java.util.List;

/**
 * 路由实现：<a href="https://www.cnblogs.com/itboys/p/6369804.html">几种简单的负载均衡算法及其Java代码实现</a>
 */
public interface Router {
    ConnectionWrapper select(List<ConnectionWrapper> connectionWrappers);

    ConnectionWrapper select(List<ConnectionWrapper> connectionWrappers, String serviceName);
}
