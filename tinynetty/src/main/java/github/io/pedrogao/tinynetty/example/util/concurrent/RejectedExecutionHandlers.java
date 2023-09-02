package github.io.pedrogao.tinynetty.example.util.concurrent;

import java.util.concurrent.RejectedExecutionException;

public class RejectedExecutionHandlers {

    private static final RejectedExecutionHandler REJECT = (task, executor) -> {
        throw new RejectedExecutionException();
    };

    private RejectedExecutionHandlers() {
    }

    public static RejectedExecutionHandler reject() {
        return REJECT;
    }
}
