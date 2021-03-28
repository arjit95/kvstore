package me.arjit.kv.discovery;

public interface DiscoveryClient {
    void start();
    void stop();
    void register(String hostname, String appName) throws Exception;
}
