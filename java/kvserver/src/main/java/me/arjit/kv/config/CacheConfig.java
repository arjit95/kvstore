package me.arjit.kv.config;

import me.arjit.kv.models.Context;
import me.arjit.kv.store.Cache;
import me.arjit.kv.strategies.data.CacheStrategy;
import me.arjit.kv.strategies.data.LRU;
import me.arjit.kv.strategies.replication.NoReplication;
import me.arjit.kv.strategies.replication.NodeReplication;
import me.arjit.kv.utils.ClusterInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {
    @Bean(name = "cacheStrategyBean")
    @ConditionalOnProperty(prefix= "kvstore.cache", name = "strategy", havingValue = "lru")
    public static CacheStrategy<byte[]> cacheStrategyConfig() {
        CacheStrategy<byte[]> strategy = new LRU<>();

        Cache<byte[]> cache = new Cache<>(strategy);
        cache.setLimit(10000);
        if (ClusterInfo.getInstance().isLeader()) {
            cache.setReplicationStrategy(new NodeReplication<>());
        } else {
            cache.setReplicationStrategy(new NoReplication<>());
        }

        Context.getContext().setCacheStore(cache);
        return strategy;
    }
}
