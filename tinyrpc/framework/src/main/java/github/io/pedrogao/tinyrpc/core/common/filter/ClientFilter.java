package github.io.pedrogao.tinyrpc.core.common.filter;

import github.io.pedrogao.tinyrpc.core.common.Invocation;

public interface ClientFilter extends Filter {
    boolean handle(Invocation invocation);
}
