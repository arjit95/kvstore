package me.arjit.kv.discovery;

import me.arjit.kv.models.Server;

public interface DiscoveryListener {
    void add(Server server);
    void remove(Server server);
    void modify(Server old, Server data);
}
