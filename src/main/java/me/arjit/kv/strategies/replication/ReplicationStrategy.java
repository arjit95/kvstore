package me.arjit.kv.strategies.replication;

import me.arjit.kv.models.CacheEntry;

public interface ReplicationStrategy<T> {
    void add(CacheEntry<T> entry);
    void get(String key);
    String getName();
}
