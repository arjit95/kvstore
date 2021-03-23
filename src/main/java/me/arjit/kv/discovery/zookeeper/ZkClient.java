package me.arjit.kv.discovery.zookeeper;

import lombok.extern.slf4j.Slf4j;
import me.arjit.kv.config.Constants;
import me.arjit.kv.discovery.DiscoveryClient;
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

@Slf4j
public class ZkClient implements DiscoveryClient {
    private static CuratorFramework client;
    final private CuratorFramework cf;
    private CuratorCacheListener listener;
    private CuratorCache cache;

    public ZkClient(String hostname) {
        this(hostname, 3);
    }

    public ZkClient(String hostname, int retries) {
        RetryPolicy policy = new ExponentialBackoffRetry(1000, retries);
        cf = CuratorFrameworkFactory.newClient(hostname, policy);
    }

    @Override
    public void stop() {
        this.removeListener();
        cf.close();
    }

    @Override
    public void start() {
        cf.start();
        this.addListener(new ZkChangeListenerImpl());
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

    private void removeListener() {
        if (this.listener == null) {
            return;
        }

        this.cache.listenable().removeListener(this.listener);
        this.listener = null;
        this.cache.close();
        this.cache = null;
    }

    private void addListener(DiscoveryListener listener) {
        this.listener = CuratorCacheListener.builder().forAll((type, oldData, data) -> {
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

        this.cache = CuratorCache.build(cf, Constants.ZOOKEEPER_LEADER);
        this.cache.listenable().addListener(this.listener);
        this.cache.start();
    }
}
