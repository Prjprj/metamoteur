package com.metamoteur.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entité représentant un résultat de recherche
 * Correspond à un lien retourné par un moteur de recherche
 */
@Entity
@Table(name = "search_results", indexes = {
        @Index(name = "idx_search_id", columnList = "search_id"),
        @Index(name = "idx_url", columnList = "url(255)"),
        @Index(name = "idx_rank", columnList = "rank")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "search")
@EqualsAndHashCode(of = "id")
public class SearchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "search_id", nullable = false)
    private Search search;

    @Column(nullable = false, length = 2000)
    private String url;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer rank;

    @Column(nullable = false)
    @Builder.Default
    private Integer clickCount = 0;

    /**
     * Incrémente le compteur de clics de manière thread-safe
     */
    public synchronized void incrementClick() {
        this.clickCount++;
    }

    /**
     * Calcule un score basé sur rang et clics
     */
    public double calculateScore() {
        // Score = (nombre de clics * 10) - rang
        // Plus de clics = meilleur score
        // Rang plus bas (1 mieux que 20) = meilleur score
        return (clickCount * 10.0) - rank;
    }
}