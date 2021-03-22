package me.arjit.kv.strategies.replication;

import lombok.extern.slf4j.Slf4j;
import me.arjit.kv.models.CacheEntry;
import me.arjit.kv.utils.ClusterInfo;

import java.util.List;

@Slf4j
public class NodeReplication<T> implements ReplicationStrategy<T> {
    @Override
    public void add(CacheEntry<T> entry) {
        List<String> members = ClusterInfo.getInstance().getMembers();
        String nodeName = ClusterInfo.getInstance().getName();
        for (String member: members) {
            if (member.equals(nodeName)) {
                continue;
            }

            log.debug("Replicating data to {}", member);
        }
    }

    @Override
    public void get(String key) {
        List<String> members = ClusterInfo.getInstance().getMembers();
        String nodeName = ClusterInfo.getInstance().getName();
        for (String member: members) {
            if (member.equals(nodeName)) {
                continue;
            }

            log.debug("Replicating data to {}", member);
        }
    }

    @Override
    public String getName() {
        return "Node replication";
    }
}
