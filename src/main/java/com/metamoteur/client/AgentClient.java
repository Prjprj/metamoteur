package com.metamoteur.client;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.metamoteur.controller.AgentController;
import com.metamoteur.dto.SearchResultDTO;
import com.metamoteur.exception.AgentCommunicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Client pour communication avec d'autres agents MetaMoteur
 * Compatible avec le protocole XML original
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AgentClient {

    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper;

    @Value("${metamoteur.agents.contact-timeout-seconds:5}")
    private int timeoutSeconds;

    /**
     * Contacte un agent distant pour obtenir une permutation
     */
    public List<SearchResultDTO> requestPermutation(
            String agentUrl,
            String keywords,
            List<SearchResultDTO> currentResults
    ) {
        log.info("Contacting agent at {} for keywords: {}", agentUrl, keywords);

        long startTime = System.currentTimeMillis();

        try {
            // Construction de la requête
            AgentController.AgentSearchRequest request =
                    AgentController.AgentSearchRequest.builder()
                            .keywords(keywords)
                            .results(currentResults)
                            .build();

            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.setAccept(List.of(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON));

            HttpEntity<AgentController.AgentSearchRequest> entity =
                    new HttpEntity<>(request, headers);

            // Appel HTTP
            String endpoint = agentUrl.endsWith("/")
                    ? agentUrl + "api/agent/search"
                    : agentUrl + "/api/agent/search";

            ResponseEntity<AgentController.AgentSearchResponse> response =
                    restTemplate.exchange(
                            endpoint,
                            HttpMethod.POST,
                            entity,
                            AgentController.AgentSearchResponse.class
                    );

            long duration = System.currentTimeMillis() - startTime;

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw AgentCommunicationException.invalidResponse(
                        agentUrl,
                        "Status: " + response.getStatusCode()
                );
            }

            List<SearchResultDTO> permutedResults = response.getBody().getResults();

            log.info("Agent {} responded in {}ms with {} results",
                    agentUrl, duration, permutedResults.size());

            return permutedResults;

        } catch (ResourceAccessException e) {
            log.error("Agent {} unreachable: {}", agentUrl, e.getMessage());
            throw AgentCommunicationException.timeout(agentUrl);

        } catch (HttpClientErrorException e) {
            log.error("Agent {} returned error: {}", agentUrl, e.getStatusCode());
            throw AgentCommunicationException.invalidResponse(
                    agentUrl,
                    "HTTP " + e.getStatusCode()
            );

        } catch (Exception e) {
            log.error("Unexpected error contacting agent: {}", agentUrl, e);
            throw new AgentCommunicationException(agentUrl, e.getMessage(), e);
        }
    }

    /**
     * Ping un agent pour vérifier sa disponibilité
     */
    public boolean ping(String agentUrl) {
        try {
            String endpoint = agentUrl.endsWith("/")
                    ? agentUrl + "api/agent/ping"
                    : agentUrl + "/api/agent/ping";

            ResponseEntity<AgentController.PingResponse> response =
                    restTemplate.getForEntity(endpoint, AgentController.PingResponse.class);

            return response.getStatusCode() == HttpStatus.OK
                    && response.getBody() != null
                    && "UP".equals(response.getBody().getStatus());

        } catch (Exception e) {
            log.debug("Agent {} ping failed: {}", agentUrl, e.getMessage());
            return false;
        }
    }
}