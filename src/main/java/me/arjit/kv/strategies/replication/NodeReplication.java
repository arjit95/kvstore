package me.arjit.kv.strategies.replication;

import lombok.extern.slf4j.Slf4j;
import me.arjit.kv.models.CacheEntry;
import me.arjit.kv.models.Server;
import me.arjit.kv.utils.ClusterInfo;

@Slf4j
public class NodeReplication<T> implements ReplicationStrategy<T> {
    @Override
    public void add(CacheEntry<T> entry) {
        Server[] members = ClusterInfo.getInstance().getMembers();
        String nodeName = ClusterInfo.getInstance().getName();
        for (Server member: members) {
            if (member.getName().equals(nodeName)) {
                continue;
            }

            log.debug("Replicating data to {}", member.getName());
        }
    }

    @Override
    public void get(String key) {
        Server[] members = ClusterInfo.getInstance().getMembers();
        String nodeName = ClusterInfo.getInstance().getName();
        for (Server member: members) {
            if (member.equals(nodeName)) {
                continue;
            }

            log.debug("Replicating data to {}", member.getName());
        }
    }

    @Override
    public String getName() {
        return "Node replication";
    }
}
