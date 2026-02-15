# API Routes Documentation

## Recherche Publique

### POST /api/search
Effectue une recherche avec permutation locale

**Request:**
```json
{
  "query": "spring boot",
  "maxResults": 20,
  "engine": "google",
  "useCache": true,
  "contactAgents": true
}
```

**Response:**
```json
{
  "query": "spring boot",
  "results": [...],
  "count": 20,
  "timestamp": "2024-01-15T10:30:00Z",
  "fromCache": false,
  "engine": "google",
  "metrics": {
    "searchTimeMs": 1234,
    "agentsContacted": 2,
    "agentsResponded": 1,
    "localPermutationApplied": true
  }
}
```

### GET /api/search/paginated
Recherche avec pagination

**Parameters:**
- `q`: Query string (required)
- `page`: Page number (default: 0)
- `size`: Results per page (default: 20, max: 100)

### GET /api/search/suggestions
Autocomplétion basée sur l'historique

**Parameters:**
- `q`: Query prefix (min 2 characters)

**Response:**
```json
["spring boot tutorial", "spring boot microservices", ...]
```

### GET /api/search/redirect
Redirection avec tracking

**Parameters:**
- `url`: Target URL (must be http/https)

**Response:** 302 redirect

### GET /api/search/health
Health check du service de recherche

**Response:**
```json
{
  "status": "UP",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## Communication Inter-Agents

### POST /api/agent/search
Endpoint pour autres agents MetaMoteur

**Request (XML ou JSON):**
```json
{
  "keywords": "java programming",
  "results": [...]
}
```

**Response:**
```json
{
  "keywords": "java programming",
  "results": [...] // Permutés selon historique local
}
```

### GET /api/agent/ping
Vérification disponibilité agent

**Response:**
```json
{
  "status": "UP",
  "version": "2.0.0"
}
```

## Analytics

### GET /api/analytics/timeline
Statistiques temporelles

**Parameters:**
- `start`: ISO 8601 datetime
- `end`: ISO 8601 datetime

### GET /api/analytics/top-urls
URLs les plus cliquées

**Parameters:**
- `limit`: Number of results (default: 10)

### GET /api/analytics/trending
Recherches tendance

**Parameters:**
- `days`: Period in days (default: 7)

## Administration (nécessite authentification)

### POST /api/admin/cleanup
Nettoyage manuel des données

**Parameters:**
- `days`: Retention period (default: 30)

### GET /api/admin/stats
Statistiques administrateur détaillées

## Actuator (Spring Boot)

### GET /actuator/health
Health check global

### GET /actuator/metrics
Métriques Prometheus

### GET /actuator/info
Informations application