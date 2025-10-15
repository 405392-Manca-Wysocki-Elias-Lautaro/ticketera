-- Crear schema si no existe
CREATE SCHEMA IF NOT EXISTS auth;
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

SET search_path TO auth;

-- =========================================================
--  TABLE: roles
-- =========================================================
CREATE TABLE auth.roles (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code            VARCHAR(50) NOT NULL UNIQUE,
    name            VARCHAR(50) NOT NULL UNIQUE,
    description     TEXT,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

-- =========================================================
--  TABLE: users
-- =========================================================
CREATE TABLE auth.users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    email_verified  BOOLEAN DEFAULT FALSE,
    is_active       BOOLEAN DEFAULT TRUE,
    password_hash   VARCHAR(255) NOT NULL,
    role_id         UUID NOT NULL,
    mfa_secret      VARCHAR(255),
    mfa_enabled     BOOLEAN DEFAULT FALSE,
    last_login_at   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- =========================================================
--  TABLE: email_verification_tokens
-- =========================================================
CREATE TABLE auth.email_verification_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash TEXT NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- =========================================================
--  TABLE: api_keys
-- =========================================================
CREATE TABLE auth.api_keys (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organizer_id    UUID NOT NULL,
    name            VARCHAR(255) NOT NULL,
    token_hash      TEXT NOT NULL UNIQUE,
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
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    api_key_id  UUID NOT NULL,
    scope       VARCHAR(255) NOT NULL,
    FOREIGN KEY (api_key_id)
        REFERENCES auth.api_keys (id)
        ON DELETE CASCADE
);

-- =========================================================
--  TABLE: refresh_tokens
-- =========================================================
CREATE TABLE auth.refresh_tokens (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL,
    token_hash      TEXT NOT NULL UNIQUE,
    ip_address      VARCHAR(50),
    user_agent      TEXT,
    remembered      BOOLEAN DEFAULT FALSE,
    revoked         BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    expires_at      TIMESTAMPTZ NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =========================================================
--  TABLE: password_reset_tokens
-- =========================================================
CREATE TABLE auth.password_reset_tokens (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL,
    token_hash      TEXT NOT NULL UNIQUE,
    used            BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    expires_at      TIMESTAMPTZ NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =========================================================
--  TABLE: login_attempts
-- =========================================================
CREATE TABLE auth.login_attempts (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID,
    email           VARCHAR(255),
    success         BOOLEAN NOT NULL,
    ip_address      VARCHAR(50),
    user_agent      TEXT,
    attempted_at    TIMESTAMPTZ DEFAULT NOW(),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- =========================================================
--  TABLE: audit_logs
-- =========================================================
CREATE TABLE auth.audit_logs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID,
    action_code     VARCHAR(20) NOT NULL,
    action          VARCHAR(100) NOT NULL,
    description     TEXT,
    ip_address      VARCHAR(50),
    user_agent      TEXT,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE auth.trusted_devices (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    ip_address  VARCHAR(50),
    user_agent  TEXT,
    location    TEXT,
    trusted     BOOLEAN NOT NULL DEFAULT TRUE,
    revoked_at  TIMESTAMPTZ,
    last_login  TIMESTAMPTZ DEFAULT now(),
    created_at  TIMESTAMPTZ DEFAULT now(),

    version BIGINT NOT NULL DEFAULT 0
);


-- =========================================================
--  INDEXES
-- =========================================================
CREATE INDEX idx_api_keys_organizer_id ON auth.api_keys (organizer_id);
CREATE INDEX idx_api_keys_token_hash ON auth.api_keys (token_hash);
CREATE INDEX idx_api_key_scopes_api_key_id ON auth.api_key_scopes (api_key_id);
CREATE INDEX idx_trusted_devices_user_agent_ip ON auth.trusted_devices (user_id, user_agent, ip_address);
CREATE INDEX idx_refresh_token_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_token_token_hash ON refresh_tokens(token_hash);
CREATE INDEX idx_refresh_token_expires_at ON refresh_tokens(expires_at);
CREATE INDEX idx_trusted_devices_user_id ON auth.trusted_devices (user_id);
CREATE INDEX idx_trusted_devices_fingerprint 
    ON auth.trusted_devices (user_id, ip_address, user_agent)
    WHERE trusted = TRUE;
