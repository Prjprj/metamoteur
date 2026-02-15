package com.metamoteur.client;

import com.metamoteur.dto.SearchResultDTO;
import com.metamoteur.exception.ParsingException;
import com.metamoteur.exception.SearchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Client de parsing HTML avec JSoup
 * Fallback quand l'API Google n'est pas disponible
 * <p>
 * ATTENTION: Le scraping peut violer les ToS des moteurs de recherche.
 * À utiliser uniquement en dev/test ou avec permission explicite.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JsoupSearchClient implements SearchProvider {

    @Value("${jsoup.user-agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36}")
    private String userAgent;

    @Value("${jsoup.timeout-ms:10000}")
    private int timeoutMs;

    @Value("${jsoup.enabled:false}")
    private boolean enabled;

    @Override
    @Cacheable(value = "jsoupSearchResults", key = "#query + '_' + #maxResults", unless = "#result == null || #result.isEmpty()")
    public List<SearchResultDTO> search(String query, int maxResults) {
        log.info("JSoup search: query='{}', maxResults={}", query, maxResults);

        if (!enabled) {
            log.warn("JSoup client is disabled");
            return List.of();
        }

        try {
            // Utilisation de DuckDuckGo (plus permissif que Google)
            String searchUrl = buildDuckDuckGoUrl(query);

            Document doc = Jsoup.connect(searchUrl)
                    .userAgent(userAgent)
                    .timeout(timeoutMs)
                    .referrer("https://www.google.com")
                    .get();

            return parseResults(doc, maxResults);

        } catch (IOException e) {
            log.error("JSoup parsing failed for query: {}", query, e);
            throw ParsingException.htmlParsingFailed(query, e);

        } catch (Exception e) {
            log.error("Unexpected error in JSoup client", e);
            throw SearchException.engineFailure("JSoup", e);
        }
    }

    @Override
    public String getProviderName() {
        return "JSoup HTML Parser (DuckDuckGo)";
    }

    @Override
    public int getPriority() {
        return 3; // Lower priority (fallback)
    }

    @Override
    public boolean isAvailable() {
        return enabled;
    }

    /**
     * Construction de l'URL DuckDuckGo
     */
    private String buildDuckDuckGoUrl(String query) {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        return "https://html.duckduckgo.com/html/?q=" + encodedQuery;
    }

    /**
     * Parsing des résultats DuckDuckGo
     */
    private List<SearchResultDTO> parseResults(Document doc, int maxResults) {
        List<SearchResultDTO> results = new ArrayList<>();

        // Sélecteurs CSS pour DuckDuckGo
        Elements resultElements = doc.select(".result");

        int rank = 1;
        for (Element element : resultElements) {
            if (results.size() >= maxResults) {
                break;
            }

            try {
                Element titleElement = element.selectFirst(".result__a");
                Element snippetElement = element.selectFirst(".result__snippet");

                if (titleElement == null) {
                    continue;
                }

                String url = titleElement.absUrl("href");
                String title = titleElement.text();
                String description = snippetElement != null ? snippetElement.text() : "";

                // Filtrer les résultats invalides
                if (url.isEmpty() || url.startsWith("javascript:")) {
                    continue;
                }

                SearchResultDTO result = SearchResultDTO.builder()
                        .url(url)
                        .title(title)
                        .description(description)
                        .rank(rank++)
                        .clickCount(0)
                        .score(0.0)
                        .source("DuckDuckGo (JSoup)")
                        .build();

                results.add(result);

            } catch (Exception e) {
                log.warn("Failed to parse result element", e);
                // Continue avec le prochain résultat
            }
        }

        log.info("JSoup parsed {} results", results.size());
        return results;
    }
}