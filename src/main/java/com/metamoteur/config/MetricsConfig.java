package com.metamoteur.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration des métriques Micrometer/Prometheus
 */
@Configuration
public class MetricsConfig {

    @Value("${spring.application.name:metamoteur}")
    private String applicationName;

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
                .commonTags(List.of(
                        Tag.of("application", applicationName),
                        Tag.of("environment", getEnvironment())
                ));
    }

    private String getEnvironment() {
        String env = System.getProperty("spring.profiles.active");
        return env != null ? env : "dev";
    }
}