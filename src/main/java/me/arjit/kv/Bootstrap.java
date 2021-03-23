package me.arjit.kv;

import lombok.extern.slf4j.Slf4j;
import me.arjit.kv.config.Config;
import me.arjit.kv.config.Constants;
import me.arjit.kv.discovery.zookeeper.Utils;
import me.arjit.kv.models.Context;
import me.arjit.kv.store.ByteStore;
import me.arjit.kv.store.Cache;
import me.arjit.kv.strategies.replication.NoReplication;
import me.arjit.kv.strategies.replication.NodeReplication;
import me.arjit.kv.utils.ClusterInfo;
import me.arjit.kv.discovery.zookeeper.ZkChangeListenerImpl;
import me.arjit.kv.discovery.zookeeper.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class Bootstrap {
    @Autowired
    private Config config;

    @PostConstruct
    public void init() throws Exception {
        initContext();
    }

    private void initContext() throws Exception {
        Cache<byte[]> store = ByteStore.getInstance();
        store.setLimit(10000);

        ZkClient zkc = new ZkClient(config.getConfigValue(Constants.ZOOKEEPER_HOST));
        initZookeeperConfig(zkc);

        Context ctx = Context.getContext();
        ctx.setCacheStore(store);
        ctx.setZookeeperClient(zkc);

        if (ClusterInfo.getInstance().isLeader()) {
            store.setReplicationStrategy(new NodeReplication<>());
        } else {
            store.setReplicationStrategy(new NoReplication<>());
        }
    }

    private void initZookeeperConfig(ZkClient zkc) throws Exception {
        String path = Constants.ZOOKEEPER_LEADER  + "/" + config.getConfigValue(Constants.APPLICATION_NAME);

        zkc.start();
        String nodePath = zkc.create(path, "localhost:" + config.getConfigValue(Constants.SERVER_PORT));
        log.debug("Registered client {} with zookeeper", nodePath);

        ClusterInfo.getInstance().setName(Utils.getNameFromPath(nodePath));
        zkc.addListener(Constants.ZOOKEEPER_LEADER, new ZkChangeListenerImpl());
    }
}
