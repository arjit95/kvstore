package me.arjit.kv.models;

import me.arjit.kv.config.environment.Env;
import me.arjit.kv.config.ContextConfig;
import me.arjit.kv.discovery.DiscoveryClient;
import me.arjit.kv.store.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Context {
    @Autowired
    public Env env;

    private Cache<byte[]> cacheStore;
    private DiscoveryClient discoveryClient;
    private static Context context;

    public Context() {

    }

    public DiscoveryClient getDiscoveryClient() {
        return discoveryClient;
    }

    public void setDiscoveryClient(DiscoveryClient ds) {
        this.discoveryClient = ds;
    }

    public Cache<byte[]> getCacheStore() {
        return cacheStore;
    }

    public void setCacheStore(Cache<byte[]> cacheStore) {
        this.cacheStore = cacheStore;
    }

    public static Context getContext() {
        if (context == null) {
            ApplicationContext ctx = new AnnotationConfigApplicationContext(ContextConfig.class, Env.class);
            context = ctx.getBean(Context.class);
        }

        return context;
    }
}
