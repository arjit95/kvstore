package me.arjit.kv.strategies.data;

import me.arjit.kv.models.CacheEntry;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRU<T> implements CacheStrategy<T> {
    private int numEntries;
    final private Map<String, CacheEntry<T>> entries;

    public LRU() {
        this(5000);
    }

    public LRU(int limit) {
        this.numEntries = limit;
        this.entries = new LinkedHashMap<>(limit, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, CacheEntry<T>> eldest) {
                return size() > numEntries;
            }
        };
    }

    @Override
    public boolean put(CacheEntry<T> entry) {
        this.entries.put(entry.getKey(), entry);
        return true;
    }

    @Override
    public CacheEntry<T> remove(String key) {
        return this.entries.remove(key);
    }

    @Override
    public void limit(int entries) {
        this.numEntries = entries;
    }

    @Override
    public int size() {
        return this.entries.size();
    }

    @Override
    public CacheEntry<T> get(String key) {
        return this.entries.getOrDefault(key, null);
    }
}
