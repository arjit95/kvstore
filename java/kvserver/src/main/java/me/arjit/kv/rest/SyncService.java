package me.arjit.kv.rest;

import me.arjit.kv.models.CacheEntry;
import me.arjit.kv.models.Server;
import me.arjit.kv.config.SyncServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class SyncService {
    private static SyncService instance;

    @Autowired
    private RestTemplate restTemplate;

    public <T> CompletableFuture<String> put(Server server, CacheEntry<T> body) {
        String endpoint = server.getAddress() + "/api/cache/put";
        String response = restTemplate.postForObject(endpoint, body, String.class);
        return CompletableFuture.completedFuture(response);
    }

    public <T> CompletableFuture<CacheEntry<T>> get(Server server, String key) {
        String endpoint = server.getAddress() + "/api/cache/get?key=" + key;
        ParameterizedTypeReference<CacheEntry<T>> responseType = new ParameterizedTypeReference<>() {};
        CacheEntry<T> response = restTemplate.exchange(endpoint, HttpMethod.GET, null, responseType).getBody();
        return CompletableFuture.completedFuture(response);
    }

    public static SyncService getInstance() {
        if (instance == null) {
            ApplicationContext ctx = new AnnotationConfigApplicationContext(SyncServiceConfig.class, Client.class);
            instance = ctx.getBean(SyncService.class);
        }

        return instance;
    }
}
