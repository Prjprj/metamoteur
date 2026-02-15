package com.metamoteur.service;

import com.metamoteur.client.AgentClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service de découverte et monitoring des agents
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AgentDiscoveryService {

    private final AgentClient agentClient;
    // Agents actifs (thread-safe)
    private final List<String> activeAgents = new CopyOnWriteArrayList<>();
    // Cache de statut (thread-safe)
    private final ConcurrentHashMap<String, Boolean> agentStatus = new ConcurrentHashMap<>();
    @Value("${metamoteur.agents.known-agents:}")
    private List<String> knownAgents;

    /**
     * Vérification périodique des agents (toutes les 5 minutes)
     */
    @Scheduled(fixedRate = 300000)
    public void checkAgentAvailability() {
        log.debug("Checking agent availability");

        activeAgents.clear();

        for (String agentUrl : knownAgents) {
            boolean isActive = agentClient.ping(agentUrl);
            agentStatus.put(agentUrl, isActive);

            if (isActive) {
                activeAgents.add(agentUrl);
                log.debug("Agent {} is active", agentUrl);
            } else {
                log.warn("Agent {} is inactive", agentUrl);
            }
        }

        log.info("Active agents: {}/{}", activeAgents.size(), knownAgents.size());
    }

    /**
     * Récupère la liste des agents actifs
     */
    public List<String> getActiveAgents() {
        return new ArrayList<>(activeAgents);
    }

    /**
     * Vérifie si un agent est actif
     */
    public boolean isAgentActive(String agentUrl) {
        return agentStatus.getOrDefault(agentUrl, false);
    }

    /**
     * Ajoute un agent dynamiquement
     */
    public void registerAgent(String agentUrl) {
        if (!knownAgents.contains(agentUrl)) {
            knownAgents.add(agentUrl);
            log.info("Registered new agent: {}", agentUrl);
        }
    }
}