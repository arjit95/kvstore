package me.arjit.kv.strategies.replication;

import lombok.extern.slf4j.Slf4j;
import me.arjit.kv.models.CacheEntry;
import me.arjit.kv.models.Server;
import me.arjit.kv.rest.SyncService;
import me.arjit.kv.utils.ClusterInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Node replication will replicate the data across all follower
 * nodes when this node will receive the data. Mostly this will
 * happen when this node is the leader
 */
@Slf4j
public class NodeReplication<T> implements ReplicationStrategy<T> {
    final private SyncService service = SyncService.getInstance();

    @Override
    public void add(CacheEntry<T> entry) {
        Server[] members = ClusterInfo.getInstance().getMembers();
        String nodeName = ClusterInfo.getInstance().getName();
        List<CompletableFuture<String>> allFutures = new ArrayList<>();

        for (Server member: members) {
            if (member.getName().equals(nodeName)) {
                continue;
            }

            log.debug("Replicating data to {} at address {}", member.getName(), member.getAddress());
            allFutures.add(service.put(member, entry));
        }

        CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).join();
    }

    @Override
    public void get(String key) {
        Server[] members = ClusterInfo.getInstance().getMembers();
        String nodeName = ClusterInfo.getInstance().getName();
        List<CompletableFuture<byte[]>> allFutures = new ArrayList<>();

        for (Server member: members) {
            if (member.getName().equals(nodeName)) {
                continue;
            }

            log.debug("Replicating data to {} at address {}", member.getName(), member.getAddress());
            allFutures.add(service.get(member, key));
        }

        CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).join();
    }

    @Override
    public String getName() {
        return "Node replication";
    }
}
