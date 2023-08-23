package github.io.pedrogao.tinyrpc.core.common.filter;

import github.io.pedrogao.tinyrpc.core.common.Invocation;

public interface ServerFilter extends Filter {
    boolean handle(Invocation invocation);
}
