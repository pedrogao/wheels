package github.io.pedrogao.tinyrpc.core.client;

import github.io.pedrogao.tinyrpc.core.common.Invocation;
import github.io.pedrogao.tinyrpc.core.common.filter.ClientFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogFilter implements ClientFilter {
    private final Logger log = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public boolean handle(Invocation invocation) {
        log.info("call {} {}", invocation.getTargetServiceName(), invocation.getTargetMethod());
        return true;
    }
}
