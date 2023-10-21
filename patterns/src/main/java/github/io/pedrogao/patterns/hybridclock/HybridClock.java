package github.io.pedrogao.patterns.hybridclock;

public class HybridClock {
    private final SystemClock systemClock;
    private HybridTimestamp latestTime;

    public HybridClock(SystemClock systemClock) {
        this.systemClock = new SystemClock();
        this.latestTime = new HybridTimestamp(systemClock.now(), 0);
    }

    public synchronized HybridTimestamp now() {
        long currentTimeMillis = systemClock.now();
        if (latestTime.getWallClockTime() >= currentTimeMillis) {
            latestTime = latestTime.addTicks(1);
        } else {
            latestTime = new HybridTimestamp(currentTimeMillis, 0);
        }
        return latestTime;
    }

    public synchronized HybridTimestamp tick(HybridTimestamp requestTime) {
        long nowMillis = systemClock.now();
        HybridTimestamp now = HybridTimestamp.fromSystemTime(nowMillis);
        latestTime = max(now, requestTime, latestTime);
        latestTime = latestTime.addTicks(1);
        return latestTime;
    }

    private HybridTimestamp max(HybridTimestamp... times) {
        HybridTimestamp maxTime = times[0];
        for (int i = 1; i < times.length; i++) {
            maxTime = maxTime.max(times[i]);
        }
        return maxTime;
    }
}
