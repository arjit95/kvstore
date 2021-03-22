package me.arjit.kv;

import me.arjit.kv.config.Config;
import me.arjit.kv.config.Constants;
import me.arjit.kv.models.Context;
import me.arjit.kv.store.ByteStore;
import me.arjit.kv.store.Cache;
import me.arjit.kv.zookeeper.ZkChangeListenerImpl;
import me.arjit.kv.zookeeper.ZkClient;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
public class KvApplication {
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
		store.setLimit(10000);

		ZkClient zkc = new ZkClient(config.getConfigValue(Constants.ZOOKEEPER_HOST));
		initZookeeperConfig(zkc);

		Context ctx = Context.getContext();
		ctx.setCacheStore(store);
		ctx.setZookeeperClient(zkc);
	}

	private static void initZookeeperConfig(ZkClient zkc) throws Exception {
		String path = Constants.ZOOKEEPER_LEADER + config.getConfigValue(Constants.APPLICATION_NAME);
		zkc.create(path, "localhost:" + config.getConfigValue(Constants.SERVER_PORT));
		zkc.addListener(path, new ZkChangeListenerImpl());
	}
}
