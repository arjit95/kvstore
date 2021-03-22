package me.arjit.kv.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

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

    public void addListener(String path, ZkChangeListener listener) {
        CuratorCacheListener cacheListener = CuratorCacheListener.builder().forAll(new CuratorCacheListener() {
            @Override
            public void event(Type type, ChildData oldData, ChildData data) {
                switch(type) {
                    case NODE_CHANGED:
                        listener.modify(oldData, data);
                        break;
                    case NODE_CREATED:
                        listener.add(data);
                        break;
                    case NODE_DELETED:
                        listener.remove(oldData);
                        break;
                }
            }
        }).build();

        CuratorCache cache = CuratorCache.build(cf, path);
        cache.listenable().addListener(cacheListener);
        cache.start();
    }


}
