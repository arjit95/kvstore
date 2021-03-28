package me.arjit.kv.strategies.data;

import me.arjit.kv.models.CacheEntry;

import java.util.Map;

public interface CacheStrategy<T> {
    CacheEntry<T> remove(String key);
    boolean put(CacheEntry<T> entry);
    void limit(int entries);
    int size();
    CacheEntry<T> get(String key);
    String serialize();
    // Deserialize will come from post request majority of times, which will be json
    void deserialize(Map<String, CacheEntry<T>> values);
}
