package org.example.skillwheel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class SecurityContextConfig {

    @Bean
    public String configureSecurityContextHolder() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_THREADLOCAL);
        return SecurityContextHolder.MODE_THREADLOCAL;
    }
}