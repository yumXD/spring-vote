package com.vote.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
@Slf4j
public class AuditConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        log.info("Auditing 설정 로드...");
        return new AuditorAwareImpl();
    }
}
