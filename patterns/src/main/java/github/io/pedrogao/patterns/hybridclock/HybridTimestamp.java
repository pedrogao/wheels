package github.io.pedrogao.patterns.hybridclock;

import java.time.LocalDateTime;

public class HybridTimestamp implements Comparable<HybridTimestamp> {
    private final long wallClockTime;
    private final int ticks;

    public HybridTimestamp(long systemTime, int ticks) {
        this.wallClockTime = systemTime;
        this.ticks = ticks;
    }

    public static HybridTimestamp fromSystemTime(long systemTime) {
        // initialize ticks to -1, call `addTicks` to set it to 0
        return new HybridTimestamp(systemTime, -1);
    }

    public HybridTimestamp max(HybridTimestamp other) {
        if (this.compareTo(other) > 0)
            return this;
        return other;
    }

    public long getWallClockTime() {
        return wallClockTime;
    }

    public long getTicks() {
        return ticks;
    }

    public HybridTimestamp addTicks(int ticks) {
        return new HybridTimestamp(this.wallClockTime, this.ticks + ticks);
    }

    public LocalDateTime toDateTime() {
        return LocalDateTime.ofEpochSecond(wallClockTime / 1000, 0, null);
    }

    public long epochMillis() {
        // Compact timestamp as discussed in https://cse.buffalo.edu/tech-reports/2014-04.pdf.
        return (wallClockTime >> 16 << 16) | ((long) ticks << 48 >> 48);
    }

    @Override
    public int compareTo(HybridTimestamp other) {
        if (this.wallClockTime == other.wallClockTime)
            return Integer.compare(this.ticks, other.ticks);
        return Long.compare(this.wallClockTime, other.wallClockTime);
    }
}
