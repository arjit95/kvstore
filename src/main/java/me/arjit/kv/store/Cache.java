package me.arjit.kv.store;

import lombok.extern.slf4j.Slf4j;
import me.arjit.kv.models.CacheEntry;
import me.arjit.kv.strategies.data.CacheStrategy;
import me.arjit.kv.strategies.replication.NodeReplication;
import me.arjit.kv.strategies.replication.ReplicationStrategy;

@Slf4j
public class Cache<T> {
    final private CacheStrategy<T> strategy;
    private ReplicationStrategy<T> replicationStrategy;

    public Cache(CacheStrategy<T> strategy) {
        this(strategy, new NodeReplication<>());
    }

    public Cache(CacheStrategy<T> strategy, ReplicationStrategy<T> rs) {
        this.strategy = strategy;
        this.replicationStrategy = rs;
    }

    final public ReplicationStrategy<T> getReplicationStrategy() {
        return replicationStrategy;
    }

    final public void setReplicationStrategy(ReplicationStrategy<T> rs) {
        log.debug("Setting replication strategy to {}", rs.getName());
        this.replicationStrategy = rs;
    }

    final public void add(String key, T value) {
        CacheEntry<T> entry = new CacheEntry<>(key, value);
        this.strategy.put(entry);
        replicationStrategy.add(entry);
    }

    final public T get(String key) {
        CacheEntry<T> entry = this.strategy.get(key);
        if (entry == null) {
            return null;
        }

        replicationStrategy.get(key);
        return entry.getValue();
    }

    final public void setLimit(int limit) {
        this.strategy.limit(limit);
    }
}
