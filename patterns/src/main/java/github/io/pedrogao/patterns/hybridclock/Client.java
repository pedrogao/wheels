package github.io.pedrogao.patterns.hybridclock;

public class Client {
    private final HybridClock clock = new HybridClock(new SystemClock());

    private final Server server1;
    private final Server server2;

    public Client() {
        this.server1 = new Server(new HybridClockMVCCStore());
        this.server2 = new Server(new HybridClockMVCCStore());
    }

    public void write() {
        HybridTimestamp server1WrittenAt = server1.write("key1", "value1", clock.now());
        clock.tick(server1WrittenAt);

        HybridTimestamp server2WrittenAt = server2.write("key2", "value2", clock.now());

        assert server2WrittenAt.compareTo(server1WrittenAt) > 0;
    }
}
