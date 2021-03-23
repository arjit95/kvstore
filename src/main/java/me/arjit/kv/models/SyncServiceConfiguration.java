package me.arjit.kv.models;

import me.arjit.kv.rest.SyncService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SyncServiceConfiguration {
    @Bean
    public SyncService syncServiceBean() {
        return new SyncService();
    }
}
