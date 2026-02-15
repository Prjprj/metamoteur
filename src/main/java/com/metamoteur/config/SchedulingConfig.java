package com.metamoteur.config;

import com.metamoteur.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Configuration des tâches planifiées
 */
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulingConfig {

    private final SearchService searchService;

    @Value("${metamoteur.search.history-retention-days:30}")
    private int retentionDays;

    /**
     * Nettoyage quotidien des anciennes recherches
     * Exécuté tous les jours à 2h du matin
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupOldSearches() {
        log.info("Starting scheduled cleanup of old searches");

        try {
            int deleted = searchService.cleanupOldSearches(retentionDays);
            log.info("Cleanup complete: {} searches deleted", deleted);
        } catch (Exception e) {
            log.error("Scheduled cleanup failed", e);
        }
    }

    /**
     * Rafraîchissement du cache
     * Exécuté toutes les heures
     */
    @Scheduled(fixedRate = 3600000) // 1 heure en ms
    public void refreshCache() {
        log.debug("Cache refresh triggered");
        // TODO: Implémenter logique de refresh si nécessaire
    }

    /**
     * Monitoring des performances
     * Exécuté toutes les 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void monitorPerformance() {
        log.debug("Performance monitoring check");
        // TODO: Collecter métriques, alertes si nécessaire
    }
}