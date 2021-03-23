package me.arjit.kv.discovery.zookeeper;

import lombok.extern.slf4j.Slf4j;
import me.arjit.kv.discovery.DiscoveryListener;
import me.arjit.kv.models.Context;
import me.arjit.kv.models.Server;
import me.arjit.kv.strategies.replication.NoReplication;
import me.arjit.kv.strategies.replication.NodeReplication;
import me.arjit.kv.utils.ClusterInfo;

@Slf4j
public class ZkChangeListenerImpl implements DiscoveryListener {
    private boolean isLeader = false;

    @Override
    public void add(Server server) {
        ClusterInfo.getInstance().getMembers().add(server.getAddress());
        log.debug("Adding {} to members list", server.getName());
        updateRS();
    }

    @Override
    public void remove(Server server) {
        ClusterInfo.getInstance().removeMember(server.getAddress());
        log.debug("Removing {} from members list", server.getName());
        updateRS();
    }

    @Override
    public void modify(Server oldS, Server newS) {
        remove(oldS);
        add(newS);
    }

    private void updateRS() {
        boolean isLeaderNow = ClusterInfo.getInstance().isLeader();
        if (isLeaderNow == isLeader) {
            return;
        }

        Context ctx = Context.getContext();
        // Start replicating to other nodes once this node is elected the leader
        if (isLeaderNow) {
            log.debug("{} is the leader now", ClusterInfo.getInstance().getName());
            if (!(ctx.getCacheStore().getReplicationStrategy() instanceof NodeReplication))
                ctx.getCacheStore().setReplicationStrategy(new NodeReplication<>());
        } else { // Do not replicate any data from this node
            if (!(ctx.getCacheStore().getReplicationStrategy() instanceof NoReplication)) {
                ctx.getCacheStore().setReplicationStrategy(new NoReplication<>());
            }
        }

        isLeader = isLeaderNow;
    }
}
