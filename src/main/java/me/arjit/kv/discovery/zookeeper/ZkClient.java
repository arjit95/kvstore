package me.arjit.kv.discovery.zookeeper;

import lombok.extern.slf4j.Slf4j;
import me.arjit.kv.config.Constants;
import me.arjit.kv.discovery.DiscoveryListener;
import me.arjit.kv.models.Server;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ZkClient {
    private static CuratorFramework client;
    final private CuratorFramework cf;

    public ZkClient(String hostname) {
        this(hostname, 3);
    }

    public ZkClient(String hostname, int retries) {
        RetryPolicy policy = new ExponentialBackoffRetry(1000, retries);
        cf = CuratorFrameworkFactory.newClient(hostname, policy);
    }

    public void start() {
        cf.start();
    }

    public CuratorFramework getClient() {
        return cf;
    }

    public String create(String path, String data) throws Exception {
        return cf.create()
            .orSetData()
            .creatingParentsIfNeeded()
            .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
            .forPath(path, data.getBytes());
    }

    private Server getServer(ChildData data) {
        // Leader is not a valid server
        if (data == null || data.getPath().equals(Constants.ZOOKEEPER_LEADER)) {
            return null;
        }

        String hostname = new String(data.getData(), StandardCharsets.UTF_8);
        return Server.create(Utils.getNameFromPath(data.getPath()), hostname);
    }

    public void addListener(String path, DiscoveryListener listener) {
        CuratorCacheListener cacheListener = CuratorCacheListener.builder().forAll((type, oldData, data) -> {
            Server oldS = getServer(oldData);
            Server newS = getServer(data);

            switch(type) {
                case NODE_CHANGED:
                    if (oldS != null && newS != null) {
                        listener.modify(oldS, newS);
                    }
                    break;
                case NODE_CREATED:
                    if (newS != null) {
                        listener.add(newS);
                    }
                    break;
                case NODE_DELETED:
                    if (oldS != null) {
                        listener.remove(oldS);
                    }
                    break;
            }
        }).build();

        CuratorCache cache = CuratorCache.build(cf, path);
        cache.listenable().addListener(cacheListener);
        cache.start();
    }
}
