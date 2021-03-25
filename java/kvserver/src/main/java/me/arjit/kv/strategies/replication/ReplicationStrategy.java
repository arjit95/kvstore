package me.arjit.kv.strategies.replication;

import me.arjit.kv.models.CacheEntry;

public interface ReplicationStrategy<T> {
    /**
     * Triggered when the data is added to the cache
     * @param entry Cache entry object
     */
    void add(CacheEntry<T> entry);

    /**
     * Triggered when the data is fetched from the cache. This call could also be used
     * to update the state of the data, so this operation might also need to be replicated
     * across nodes. (Eg: When data is store using LRU/LFU)
     * @param key Cache key used to fetch the data
     */
    void get(String key);

    /**
     * Returns the human readable name of the replication strategy
     * @return Returns the name of the strategy
     */
    String getName();
}
