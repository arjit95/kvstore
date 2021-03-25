package me.arjit.kv.config;

import me.arjit.kv.models.Context;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContextConfig {
    @Bean(name = "contextBean")
    public Context contextConfig() {
        return new Context();
    }
}
