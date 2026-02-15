package com.metamoteur.controller;

import com.metamoteur.dto.AdminStatsDTO;
import com.metamoteur.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller d'administration
 * Nécessite authentification (à implémenter)
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')") // Nécessite Spring Security configuré
public class AdminController {

    private final SearchService searchService;

    /**
     * Nettoyage manuel des anciennes données
     * POST /api/admin/cleanup?days=30
     */
    @PostMapping("/cleanup")
    public ResponseEntity<CleanupResultDTO> cleanup(
            @RequestParam(defaultValue = "30") int days
    ) {
        log.info("Admin cleanup requested: days={}", days);

        if (days < 1 || days > 365) {
            return ResponseEntity.badRequest().build();
        }

        int deleted = searchService.cleanupOldSearches(days);

        CleanupResultDTO result = CleanupResultDTO.builder()
                .deletedSearches(deleted)
                .daysRetained(days)
                .build();

        return ResponseEntity.ok(result);
    }

    /**
     * Statistiques administrateur
     * GET /api/admin/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDTO> getAdminStats() {
        // TODO: Implémenter statistiques avancées
        AdminStatsDTO stats = AdminStatsDTO.builder()
                .totalSearches(0L)
                .totalResults(0L)
                .totalClicks(0L)
                .cacheHitRate(0.0)
                .averageSearchTimeMs(0L)
                .build();

        return ResponseEntity.ok(stats);
    }

    /**
     * DTO pour résultat de nettoyage
     */
    @lombok.Data
    @lombok.Builder
    public static class CleanupResultDTO {
        private Integer deletedSearches;
        private Integer daysRetained;
    }
}