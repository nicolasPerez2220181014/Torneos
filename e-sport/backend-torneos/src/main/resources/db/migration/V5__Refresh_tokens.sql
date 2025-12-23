-- V5__Refresh_tokens.sql
-- Create refresh tokens table for JWT token rotation

CREATE TABLE refresh_tokens (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    user_email VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for efficient lookups
CREATE INDEX idx_refresh_token_lookup ON refresh_tokens(token, expires_at, revoked);
CREATE INDEX idx_refresh_token_user ON refresh_tokens(user_email);

-- Cleanup expired tokens (optional, can be done via scheduled job)
-- DELETE FROM refresh_tokens WHERE expires_at < CURRENT_TIMESTAMP OR revoked = TRUE;