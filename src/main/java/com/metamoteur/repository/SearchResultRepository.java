package com.metamoteur.repository;

import com.metamoteur.entity.SearchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour les résultats de recherche
 */
@Repository
public interface SearchResultRepository extends JpaRepository<SearchResult, Long> {

    /**
     * Recherche par URL exacte
     */
    Optional<SearchResult> findByUrl(String url);

    /**
     * Tous les résultats pour une recherche donnée
     */
    List<SearchResult> findBySearchIdOrderByRankAsc(Long searchId);

    /**
     * Résultats avec le plus de clics
     */
    @Query("""
            SELECT sr FROM SearchResult sr
            WHERE sr.search.id = :searchId
            ORDER BY sr.clickCount DESC, sr.rank ASC
            """)
    List<SearchResult> findMostClickedBySearch(@Param("searchId") Long searchId);

    /**
     * Incrémentation sécurisée du compteur de clics
     */
    @Modifying
    @Query("UPDATE SearchResult sr SET sr.clickCount = sr.clickCount + 1 WHERE sr.url = :url")
    int incrementClickCountByUrl(@Param("url") String url);

    /**
     * Incrémentation par ID (plus performant)
     */
    @Modifying
    @Query("UPDATE SearchResult sr SET sr.clickCount = sr.clickCount + 1 WHERE sr.id = :id")
    int incrementClickCountById(@Param("id") Long id);

    /**
     * Recherche par URL partielle (pour similarité)
     */
    @Query("SELECT sr FROM SearchResult sr WHERE sr.url LIKE CONCAT('%', :urlPart, '%')")
    List<SearchResult> findByUrlContaining(@Param("urlPart") String urlPart);

    /**
     * URLs les plus cliquées globalement
     */
    @Query("""
            SELECT sr.url, SUM(sr.clickCount) as totalClicks
            FROM SearchResult sr
            GROUP BY sr.url
            ORDER BY totalClicks DESC
            """)
    List<Object[]> findMostClickedUrls(org.springframework.data.domain.Pageable pageable);
}