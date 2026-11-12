## Code Review

You are reviewing the following code submitted as part of a task to implement an item cache in a highly concurrent application. The anticipated load includes: thousands of reads per second, hundreds of writes per second, tens of concurrent threads.
Your objective is to identify and explain the issues in the implementation that must be addressed before deploying the code to production. Please provide a clear explanation of each issue and its potential impact on production behaviour.

```java
import java.util.concurrent.ConcurrentHashMap;

public class SimpleCache<K, V> {
    private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final long ttlMs = 60000; // 1 minute

    public static class CacheEntry<V> {
        private final V value;
        private final long timestamp;

        public CacheEntry(V value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }

        public V getValue() {
            return value;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis()));
    }

    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null) {
            if (System.currentTimeMillis() - entry.getTimestamp() < ttlMs) {
                return entry.getValue();
            }
        }
        return null;
    }

    public int size() {
        return cache.size();
    }
}
```

1. We should allow cache TTL to be set by the user instead of a static value within the class. The TTL can change depending on the nature of the data that is being cached
1. We need Null-checks on keys and values of both the `put()` and `get()` methods because we could end up wrapping a `null` value with a `CacheEntry` object. Should do a null-check in the `CacheEtnry` constructor as well.
1. there should be a size limit on the `cache` to avoid we running out of heapspace. 
   * In order to control the size, We should implement a scheduled sweeper logic to periodicaly remove the oldest entities (assuming we want LRU cache) if the cache size exceeds a max size (again a configurable parameter). 
   * If not, we need to switch the implemention from ConcurrentCacheMap to LinkedHashMap with manual synchronized blocks.
1. The `get()` method should clean up the expired data before returning `null` to prevent the cahe from continuously growing and later resulting in OutOfMemory issue. 
   * We should still implement this even if we have the above sweeper logic -- sweeper is more resource consuming than this quick cleanup.
1. The `get()` method returns `null` when the TTL is expired. This means we are expecting another component to be responsible for refreshing the cache. Let's say that's the `CacheManager`. That cache manager's `get()` method should have a cache refresh logic (`load`/`put`) if it receivees a `null` from `SimpleCache`. Considering the `load()` is expected to be a slow operation, it will increase the latency of the manager's `get()` call every minute on a particular key. Considering it is a read-heavy system, 1000s of `get()` calls on a hot-key will experience high latency every minute reducing the usefulness of the cache.
   * Tthe manager should implement a lazy-load to prevent this; i.e. return whatever it has in the cache immediately while refresh the cache asynchronously.
   * also note that the load/put could possibly override a newer write/put. We should implement a lock mechanism that favors the write over the load
1. We should emit metrics on cache hit/miss to make a data-driven decision on the cache size and TTL value.
1. minor: `System.currentTimeMillis()` is impacted by system setup like NTP adjustment. Should use `System.naoTime()` which uses epoche time measure
