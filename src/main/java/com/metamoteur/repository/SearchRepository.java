package com.metamoteur.repository;

import com.metamoteur.entity.Search;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour les recherches
 * Toutes les requêtes sont sécurisées via paramètres
 */
@Repository
public interface SearchRepository extends JpaRepository<Search, Long> {

    /**
     * Recherche par mots-clés exacts (case insensitive)
     */
    Optional<Search> findByKeywordsIgnoreCase(String keywords);

    /**
     * Recherche par similarité de mots-clés
     */
    List<Search> findByKeywordsContainingIgnoreCase(String keywords);

    /**
     * Recherche paginée par mots-clés
     */
    Page<Search> findByKeywordsContainingIgnoreCase(String keywords, Pageable pageable);

    /**
     * Recherches récentes (moins de X minutes)
     */
    List<Search> findByTimestampAfter(Instant timestamp);

    /**
     * Recherche avec Full-Text Search PostgreSQL
     */
    @Query(value = """
            SELECT s.* FROM searches s
            WHERE to_tsvector('french', s.keywords) @@ plainto_tsquery('french', :query)
            ORDER BY ts_rank(to_tsvector('french', s.keywords), plainto_tsquery('french', :query)) DESC
            LIMIT :limit
            """,
            nativeQuery = true)
    List<Search> findSimilarSearches(
            @Param("query") String query,
            @Param("limit") int limit
    );

    /**
     * Compte les recherches pour un mot-clé
     */
    @Query("SELECT COUNT(s) FROM Search s WHERE LOWER(s.keywords) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    long countByKeywordContaining(@Param("keyword") String keyword);

    /**
     * Recherches les plus populaires (par nombre de résultats cliqués)
     */
    @Query("""
            SELECT s FROM Search s
            JOIN s.results r
            WHERE s.timestamp > :since
            GROUP BY s.id
            ORDER BY SUM(r.clickCount) DESC
            """)
    Page<Search> findMostPopular(@Param("since") Instant since, Pageable pageable);

    /**
     * Nettoyage des anciennes recherches (soft delete possible)
     */
    @Modifying
    @Query("DELETE FROM Search s WHERE s.timestamp < :before")
    int deleteOldSearches(@Param("before") Instant before);

    /**
     * Statistiques temporelles
     */
    @Query("""
            SELECT DATE(s.timestamp) as date, COUNT(s) as count
            FROM Search s
            WHERE s.timestamp BETWEEN :start AND :end
            GROUP BY DATE(s.timestamp)
            ORDER BY date DESC
            """)
    List<Object[]> getSearchStatistics(
            @Param("start") Instant start,
            @Param("end") Instant end
    );
}