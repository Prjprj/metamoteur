package com.metamoteur.exception;

/**
 * Exception levée lors d'erreurs de communication avec d'autres agents
 */
public class AgentCommunicationException extends MetaMoteurException {

    private final String agentUrl;

    public AgentCommunicationException(String agentUrl, String message) {
        super(
                "AGENT_COMMUNICATION_ERROR",
                String.format("Failed to communicate with agent at %s: %s", agentUrl, message)
        );
        this.agentUrl = agentUrl;
    }

    public AgentCommunicationException(String agentUrl, String message, Throwable cause) {
        super(
                "AGENT_COMMUNICATION_ERROR",
                String.format("Failed to communicate with agent at %s: %s", agentUrl, message),
                cause
        );
        this.agentUrl = agentUrl;
    }

    public static AgentCommunicationException timeout(String agentUrl) {
        return new AgentCommunicationException(
                agentUrl,
                "Connection timeout"
        );
    }

    public static AgentCommunicationException connectionRefused(String agentUrl) {
        return new AgentCommunicationException(
                agentUrl,
                "Connection refused"
        );
    }

    public static AgentCommunicationException invalidResponse(String agentUrl, String reason) {
        return new AgentCommunicationException(
                agentUrl,
                String.format("Invalid response: %s", reason)
        );
    }

    public String getAgentUrl() {
        return agentUrl;
    }
}