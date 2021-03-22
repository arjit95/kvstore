package me.arjit.kv.zookeeper;

import lombok.extern.slf4j.Slf4j;
import me.arjit.kv.models.Context;
import me.arjit.kv.strategies.replication.NoReplication;
import me.arjit.kv.strategies.replication.NodeReplication;
import me.arjit.kv.utils.ClusterInfo;
import org.apache.catalina.Cluster;
import org.apache.curator.framework.recipes.cache.ChildData;

@Slf4j
public class ZkChangeListenerImpl implements ZkChangeListener {
    private boolean isLeader = false;

    @Override
    public void add(ChildData data) {
        ClusterInfo.getInstance().getMembers().add(data.getPath());
        log.debug("Adding {} to members list", data.getPath());
        updateRS();
    }

    @Override
    public void remove(ChildData data) {
        ClusterInfo.getInstance().getMembers().remove(data.getPath());
        log.debug("Removing {} from members list", data.getPath());
        updateRS();
    }

    @Override
    public void modify(ChildData old, ChildData data) {
        remove(old);
        add(data);
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
