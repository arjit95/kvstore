package me.arjit.kv.strategies;

import me.arjit.kv.models.CacheEntry;

public interface CacheStrategy<T> {
    CacheEntry<T> remove(String key);
    boolean put(CacheEntry<T> entry);
    void limit(int entries);
    int size();
    CacheEntry<T> get(String key);
}
