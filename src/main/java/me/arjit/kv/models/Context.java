package me.arjit.kv.models;

import me.arjit.kv.discovery.DiscoveryClient;
import me.arjit.kv.store.Cache;

public class Context {
    private Cache<byte[]> cacheStore;
    private DiscoveryClient zkClient;
    private static Context context;

    private Context() {

    }

    public DiscoveryClient getDiscoveryClient() {
        return zkClient;
    }

    public void setDiscoveryClient(DiscoveryClient zkClient) {
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
