package me.arjit.kv.config;

import me.arjit.kv.rest.SyncService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SyncServiceConfig {
    @Bean("syncServiceBean")
    public SyncService syncServiceConfig() {
        return new SyncService();
    }
}
