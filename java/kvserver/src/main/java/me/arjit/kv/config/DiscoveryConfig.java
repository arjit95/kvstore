package me.arjit.kv.config;

import lombok.extern.slf4j.Slf4j;
import me.arjit.kv.config.environment.Env;
import me.arjit.kv.config.environment.Constants;
import me.arjit.kv.discovery.DiscoveryClient;
import me.arjit.kv.discovery.zookeeper.Utils;
import me.arjit.kv.discovery.zookeeper.ZkClient;
import me.arjit.kv.models.Context;
import me.arjit.kv.utils.ClusterInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class DiscoveryConfig {
    @Bean(name = "discoveryBean")
    @ConditionalOnProperty(prefix= "kvstore.discovery", name = "client", havingValue = "zookeeper")
    public static DiscoveryClient discoveryConfig() throws Exception {
        Env env = Context.getContext().env;;
        ZkClient client =  new ZkClient(env.getValue(Constants.ZOOKEEPER_HOST));
        client.start();

        // Register this client with zookeeper
        String path = Constants.ZOOKEEPER_LEADER  + "/" + env.getValue(Constants.APPLICATION_NAME);
        String nodePath = client.create(path, "http://localhost:" + env.getValue(Constants.SERVER_PORT));

        // Attach discovery client to context
        log.debug("Registered client {} with zookeeper", nodePath);
        ClusterInfo.getInstance().setName(Utils.getNameFromPath(nodePath));
        Context.getContext().setDiscoveryClient(client);

        return client;
    }
}
