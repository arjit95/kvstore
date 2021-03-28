package me.arjit.kv.config;

import lombok.extern.slf4j.Slf4j;
import me.arjit.kv.config.environment.Env;
import me.arjit.kv.config.environment.Constants;
import me.arjit.kv.discovery.DiscoveryClient;
import me.arjit.kv.discovery.zookeeper.ZkClient;
import me.arjit.kv.models.Context;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class DiscoveryConfig {
    @Bean(name = "discoveryBean")
    @ConditionalOnProperty(prefix= "kvstore.cache.discovery", name = "client", havingValue = "zookeeper")
    public DiscoveryClient discoveryConfig() {
        Env env = Context.getContext().env;
        ZkClient client =  new ZkClient(env.getValue(Constants.ZOOKEEPER_HOST));
        Context.getContext().setDiscoveryClient(client);
        return client;
    }
}
