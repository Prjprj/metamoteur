package com.metamoteur.exception;

/**
 * Exception levée quand une ressource n'est pas trouvée
 */
public class ResourceNotFoundException extends MetaMoteurException {

    private final String resourceType;
    private final Object resourceId;

    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super(
                "RESOURCE_NOT_FOUND",
                String.format("%s not found with id: %s", resourceType, resourceId)
        );
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public static ResourceNotFoundException search(Long searchId) {
        return new ResourceNotFoundException("Search", searchId);
    }

    public static ResourceNotFoundException searchResult(Long resultId) {
        return new ResourceNotFoundException("SearchResult", resultId);
    }

    public static ResourceNotFoundException url(String url) {
        return new ResourceNotFoundException("URL", url);
    }

    public String getResourceType() {
        return resourceType;
    }

    public Object getResourceId() {
        return resourceId;
    }
}