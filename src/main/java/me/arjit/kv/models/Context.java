package me.arjit.kv.models;

import me.arjit.kv.store.Cache;
import me.arjit.kv.zookeeper.ZkClient;

public class Context {
    private Cache<byte[]> cacheStore;
    private ZkClient zkClient;
    private static Context context;

    private Context() {

    }

    public ZkClient getZookeeperClient() {
        return zkClient;
    }

    public void setZookeeperClient(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    public Cache<byte[]> getCacheStore() {
        return cacheStore;
    }

    public void setCacheStore(Cache<byte[]> cacheStore) {
        this.cacheStore = cacheStore;
    }

    public static Context getContext() {
        if (context == null) {
            context = new Context();
        }

        return context;
    }
}
