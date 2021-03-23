package me.arjit.kv.config.environment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class Env {
    @Autowired
    private Environment env;

    public String getValue(String configKey){
        return env.getProperty(configKey);
    }
}
