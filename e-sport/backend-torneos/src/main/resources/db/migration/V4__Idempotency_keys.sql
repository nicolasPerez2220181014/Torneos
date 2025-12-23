-- V4__Idempotency_keys.sql
-- Create idempotency keys table for preventing duplicate operations

CREATE TABLE idempotency_keys (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    idempotency_key VARCHAR(255) NOT NULL UNIQUE,
    response_body TEXT,
    status_code INTEGER,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for efficient lookups
CREATE INDEX idx_idempotency_key_expires ON idempotency_keys(idempotency_key, expires_at);

-- Cleanup expired keys (optional, can be done via scheduled job)
-- DELETE FROM idempotency_keys WHERE expires_at < CURRENT_TIMESTAMP;