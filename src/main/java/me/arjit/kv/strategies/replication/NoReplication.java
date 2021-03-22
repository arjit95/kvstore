package me.arjit.kv.strategies.replication;

import me.arjit.kv.models.CacheEntry;

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
