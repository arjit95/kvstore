package me.arjit.kv.strategies.replication;

import me.arjit.kv.models.CacheEntry;

/**
 * No replication strategy will occur when this node is not responsible
 * for any replication tasks
 * @param <T>
 */
public class NoReplication <T> implements ReplicationStrategy<T> {
    @Override
    public void add(CacheEntry<T> entry) {

    }

    @Override
    public void get(String key) {

    }

    @Override
    public String getName() {
        return "No replication";
    }
}
