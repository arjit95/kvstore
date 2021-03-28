package me.arjit.kv.config.environment;

public class Constants {
    public static final String ZOOKEEPER_HOST = "spring.cloud.zookeeper.connectString";
    public static final String ZOOKEEPER_LEADER = "/leader";
    public static final String PARTITION_NAME = "${kvstore.cache.partition.name}";
    public static final String HOSTNAME = "${kvstore.hostname}";
    public static final String SERVER_PORT = "${server.port}";
}
