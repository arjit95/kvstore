package me.arjit.kv.listeners;

import me.arjit.kv.config.environment.Constants;
import me.arjit.kv.discovery.DiscoveryClient;
import me.arjit.kv.models.Context;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationReady {
    @Value(Constants.PARTITION_NAME)
    private String appName;

    @Value(Constants.HOSTNAME)
    private String hostname;

    @Value(Constants.SERVER_PORT)
    private int port;

    @EventListener(ApplicationReadyEvent.class)
    public void running() {
        String address = "http://" + hostname + ":" + port;
        try {
            DiscoveryClient client = Context.getContext().getDiscoveryClient();
            client.start();
            client.register(address, appName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
