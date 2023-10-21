package github.io.pedrogao.patterns.hybridclock;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;

public class HybridClockMVCCStore {
    private final ConcurrentSkipListMap<HybridClockKey, String> kv = new ConcurrentSkipListMap<>();

    public HybridClockMVCCStore() {
    }

    public void put(String key, HybridTimestamp requestTimestamp, String value) {
        kv.put(new HybridClockKey(key, requestTimestamp), value);
    }

    public Optional<String> get(String key, HybridTimestamp requestTimestamp) {
        Map.Entry<HybridClockKey, String> versionKeys =
                kv.floorEntry(new HybridClockKey(key, requestTimestamp));

        return versionKeys == null ? Optional.empty() : Optional.of(versionKeys.getValue());
    }
}
