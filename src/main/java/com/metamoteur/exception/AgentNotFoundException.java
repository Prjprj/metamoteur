package com.metamoteur.exception;

/**
 * Exception levée quand un agent n'est pas trouvé
 */
public class AgentNotFoundException extends MetaMoteurException {

    private final String agentId;

    public AgentNotFoundException(String agentId) {
        super(
                "AGENT_NOT_FOUND",
                String.format("Agent not found: %s", agentId)
        );
        this.agentId = agentId;
    }

    public String getAgentId() {
        return agentId;
    }
}