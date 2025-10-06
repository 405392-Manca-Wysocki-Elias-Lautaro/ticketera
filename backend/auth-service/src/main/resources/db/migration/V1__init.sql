-- Crear schema si no existe
CREATE SCHEMA IF NOT EXISTS auth;

SET search_path TO auth;

-- =========================================================
--  TABLE: roles
-- =========================================================
CREATE TABLE roles (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(50) NOT NULL UNIQUE,
    name            VARCHAR(50) NOT NULL UNIQUE,
    description     TEXT,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

-- =========================================================
--  TABLE: users
-- =========================================================
CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    email_verified  BOOLEAN DEFAULT FALSE,
    password_hash   VARCHAR(255) NOT NULL,
    role_id         BIGINT NOT NULL,
    mfa_secret      VARCHAR(255),
    mfa_enabled     BOOLEAN DEFAULT FALSE,
    is_active       BOOLEAN DEFAULT TRUE,
    last_login_at   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- =========================================================
--  TABLE: email_verification_tokens
-- =========================================================
CREATE TABLE email_verification_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- =========================================================
--  TABLE: api_keys
-- =========================================================
CREATE TABLE auth.api_keys (
    id              BIGSERIAL PRIMARY KEY,
    organizer_id    BIGINT NOT NULL,
    name            VARCHAR(255) NOT NULL,
    token_hash      VARCHAR(255) NOT NULL UNIQUE,
    revoked         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_used_at    TIMESTAMPTZ,
    updated_at      TIMESTAMPTZ,
    CONSTRAINT uq_api_keys_org_name UNIQUE (organizer_id, name)
);

-- =========================================================
--  TABLE: api_key_scopes (colección de scopes)
-- =========================================================
CREATE TABLE auth.api_key_scopes (
    api_key_id      BIGINT NOT NULL,
    scope           VARCHAR(255) NOT NULL,
    PRIMARY KEY (api_key_id, scope),
    FOREIGN KEY (api_key_id)
        REFERENCES auth.api_keys (id)
        ON DELETE CASCADE
);

-- =========================================================
--  TABLE: refresh_tokens
-- =========================================================
CREATE TABLE refresh_tokens (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    token_hash      VARCHAR(255) NOT NULL UNIQUE,
    user_agent      VARCHAR(100),
    ip_address      VARCHAR(45),
    remembered      BOOLEAN DEFAULT FALSE,
    revoked         BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    expires_at      TIMESTAMPTZ NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =========================================================
--  TABLE: password_reset_tokens
-- =========================================================
CREATE TABLE password_reset_tokens (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    token_hash      VARCHAR(255) NOT NULL UNIQUE,
    used            BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    expires_at      TIMESTAMPTZ NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =========================================================
--  TABLE: login_attempts
-- =========================================================
CREATE TABLE login_attempts (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT,
    email           VARCHAR(255),
    success         BOOLEAN NOT NULL,
    ip_address      VARCHAR(45),
    user_agent      VARCHAR(100),
    attempted_at    TIMESTAMPTZ DEFAULT NOW(),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- =========================================================
--  TABLE: audit_logs
-- =========================================================
CREATE TABLE audit_logs (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT,
    action          VARCHAR(100) NOT NULL,
    description     TEXT,
    ip_address      VARCHAR(45),
    user_agent      TEXT,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- =========================================================
--  INDEXES
-- =========================================================
CREATE INDEX idx_api_keys_organizer_id ON auth.api_keys (organizer_id);
CREATE INDEX idx_api_keys_token_hash ON auth.api_keys (token_hash);
CREATE INDEX idx_api_key_scopes_api_key_id ON auth.api_key_scopes (api_key_id);
