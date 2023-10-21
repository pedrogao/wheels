package github.io.pedrogao.patterns.hybridclock;

public class Server {
    private final HybridClockMVCCStore mvccStore;
    private final HybridClock clock;

    public Server(HybridClockMVCCStore mvccStore) {
        this.clock = new HybridClock(new SystemClock());
        this.mvccStore = mvccStore;
    }

    public HybridTimestamp write(String key, String value, HybridTimestamp requestTimestamp) {
        HybridTimestamp writeAtTimestamp = clock.tick(requestTimestamp);
        mvccStore.put(key, requestTimestamp, value);
        return writeAtTimestamp;
    }
}
