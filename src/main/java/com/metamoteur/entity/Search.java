package com.metamoteur.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une recherche utilisateur
 * Remplace l'ancienne table dénormalisée BDD
 */
@Entity
@Table(name = "searches", indexes = {
        @Index(name = "idx_keywords", columnList = "keywords"),
        @Index(name = "idx_timestamp", columnList = "timestamp"),
        @Index(name = "idx_keywords_timestamp", columnList = "keywords,timestamp")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "results")
@EqualsAndHashCode(of = "id")
public class Search {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String keywords;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @OneToMany(
            mappedBy = "search",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<SearchResult> results = new ArrayList<>();

    /**
     * Méthode helper pour maintenir la cohérence bidirectionnelle
     */
    public void addResult(SearchResult result) {
        results.add(result);
        result.setSearch(this);
    }

    /**
     * Méthode helper pour retirer un résultat
     */
    public void removeResult(SearchResult result) {
        results.remove(result);
        result.setSearch(null);
    }

    /**
     * Vérifie si la recherche est récente (< 30 minutes)
     */
    public boolean isRecent() {
        return timestamp.isAfter(Instant.now().minusSeconds(1800));
    }
}