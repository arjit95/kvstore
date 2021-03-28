package me.arjit.kv.rest;

import lombok.extern.slf4j.Slf4j;
import me.arjit.kv.config.environment.Constants;
import me.arjit.kv.models.CacheEntry;
import me.arjit.kv.models.Context;
import me.arjit.kv.models.Server;
import me.arjit.kv.config.SyncServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class SyncService {
    @Value(Constants.PARTITION_NAME)
    private String appName;

    @Value(Constants.HOSTNAME)
    private String hostname;

    private static SyncService instance;

    @Autowired
    private RestTemplate restTemplate;

    public <T> CompletableFuture<String> put(Server server, CacheEntry<T> body) {
        String endpoint = server.getAddress() + "/api/cache/put";
        String response = restTemplate.postForObject(endpoint, body, String.class);
        return CompletableFuture.completedFuture(response);
    }

    public CompletableFuture<byte[]> get(Server server, String key) {
        String endpoint = server.getAddress() + "/api/cache/get?key=" + key;
        byte[] response = restTemplate.exchange(endpoint, HttpMethod.GET, null, byte[].class).getBody();
        return CompletableFuture.completedFuture(response);
    }

    public CompletableFuture<String> sync(Server server) {
        String endpoint = server.getAddress() + "/api/cache/sync";
        String body = Context.getContext().getCacheStore().getCacheStrategy().serialize();
        log.debug("Syncing {} to {}", body, server.getAddress());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        String response = restTemplate.postForObject(endpoint, request, String.class);
        return CompletableFuture.completedFuture(response);
    }

    public void download(Server server) throws Exception {
        String endpoint = server.getAddress() + "/api/cache/download";
        ParameterizedTypeReference<HashMap<String, CacheEntry<byte[]>>> responseType = new ParameterizedTypeReference<>() {};
        HashMap<String, CacheEntry<byte[]>> response = restTemplate.exchange(endpoint, HttpMethod.GET, null, responseType).getBody();
        response = CompletableFuture.completedFuture(response).get();

        Context.getContext().getCacheStore().getCacheStrategy().deserialize(response);
        Context.getContext().getDiscoveryClient().register(hostname, appName);
    }

    public static SyncService getInstance() {
        if (instance == null) {
            ApplicationContext ctx = new AnnotationConfigApplicationContext(SyncServiceConfig.class, Client.class);
            instance = ctx.getBean(SyncService.class);
        }

        return instance;
    }
}
