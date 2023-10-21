package github.io.pedrogao.patterns.hybridclock;

public class HybridClockKey implements Comparable<HybridClockKey> {
    public String key;
    private HybridTimestamp version;

    public HybridClockKey(String key, HybridTimestamp version) {
        this.key = key;
        this.version = version;
    }

    public String getKey() {
        return key;
    }

    public HybridTimestamp getVersion() {
        return version;
    }

    @Override
    public int compareTo(HybridClockKey o) {
        int keyCompare = key.compareTo(o.key);
        if (keyCompare == 0) {
            return version.compareTo(o.version);
        }
        return keyCompare;
    }
}
