package me.arjit.kv.discovery.zookeeper;

public class Utils {
    public static String getNameFromPath(String path) {
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }
}
