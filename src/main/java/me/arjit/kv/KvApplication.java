package me.arjit.kv;

import lombok.extern.slf4j.Slf4j;
import me.arjit.kv.config.Config;
import me.arjit.kv.config.Constants;
import me.arjit.kv.models.Context;
import me.arjit.kv.store.ByteStore;
import me.arjit.kv.store.Cache;
import me.arjit.kv.strategies.replication.NoReplication;
import me.arjit.kv.utils.ClusterInfo;
import me.arjit.kv.zookeeper.ZkChangeListenerImpl;
import me.arjit.kv.zookeeper.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class KvApplication {
	@Autowired
	private static Config config;

	public static void main(String[] args) {
		try {
			initContext();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		SpringApplication.run(KvApplication.class, args);
	}

	private static void initContext() throws Exception {
		Cache<byte[]> store = ByteStore.getInstance();
		store.setReplicationStrategy(new NoReplication<>());
		store.setLimit(10000);

		ZkClient zkc = new ZkClient(config.getConfigValue(Constants.ZOOKEEPER_HOST));
		initZookeeperConfig(zkc);

		Context ctx = Context.getContext();
		ctx.setCacheStore(store);
		ctx.setZookeeperClient(zkc);
	}

	private static void initZookeeperConfig(ZkClient zkc) throws Exception {
		String path = Constants.ZOOKEEPER_LEADER + config.getConfigValue(Constants.APPLICATION_NAME);
		log.debug("Registering client {} with zookeeper", path);

		zkc.start();
		String nodePath = zkc.create(path, "localhost:" + config.getConfigValue(Constants.SERVER_PORT));

		ClusterInfo.getInstance().setMembers(zkc.getClient().getChildren().forPath(Constants.ZOOKEEPER_LEADER));
		ClusterInfo.getInstance().setName(nodePath);
		zkc.addListener(Constants.ZOOKEEPER_LEADER, new ZkChangeListenerImpl());
	}
}
