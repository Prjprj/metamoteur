-- MetaMoteur 2.0 - Initial Schema

CREATE TABLE searches (
    id BIGSERIAL PRIMARY KEY,
    keywords VARCHAR(500) NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT keywords_not_empty CHECK (LENGTH(TRIM(keywords)) >= 2)
);

CREATE INDEX idx_searches_keywords ON searches(keywords);
CREATE INDEX idx_searches_timestamp ON searches(timestamp DESC);

CREATE TABLE search_results (
    id BIGSERIAL PRIMARY KEY,
    search_id BIGINT NOT NULL,
    url VARCHAR(1000) NOT NULL,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    rank INTEGER NOT NULL,
    click_count INTEGER DEFAULT 0,
    score DOUBLE PRECISION DEFAULT 0.0,
    source VARCHAR(50),
    last_clicked_at TIMESTAMP WITH TIME ZONE,
    
    CONSTRAINT fk_search 
        FOREIGN KEY (search_id) 
        REFERENCES searches(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT valid_rank CHECK (rank > 0),
    CONSTRAINT valid_click_count CHECK (click_count >= 0)
);

CREATE INDEX idx_results_search_id ON search_results(search_id);
CREATE INDEX idx_results_url ON search_results(url);
CREATE INDEX idx_results_click_count ON search_results(click_count DESC);

COMMENT ON TABLE searches IS 'Historique des recherches effectuées';
COMMENT ON TABLE search_results IS 'Résultats individuels pour chaque recherche';
