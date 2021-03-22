package me.arjit.kv.store;

import me.arjit.kv.models.CacheEntry;
import me.arjit.kv.strategies.CacheStrategy;

public class Cache<T> {
    final private CacheStrategy<T> strategy;

    public Cache(CacheStrategy<T> strategy) {
        this.strategy = strategy;
    }

    final public void add(String key, T value) {
        CacheEntry<T> entry = new CacheEntry<T>(key, value);
        this.strategy.put(entry);
    }

    final public T get(String key) {
        CacheEntry<T> entry = this.strategy.get(key);
        if (entry == null) {
            return null;
        }

        return entry.getValue();
    }

    final public void setLimit(int limit) {
        this.strategy.limit(limit);
    }
}
