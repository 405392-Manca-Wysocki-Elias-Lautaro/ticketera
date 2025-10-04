CREATE TABLE users (
  id              bigserial PRIMARY KEY,
  email           text NOT NULL UNIQUE,
  email_verified  boolean NOT NULL DEFAULT false,
  email_verification_token text,
  email_verification_expires_at timestamptz,
  last_verification_email_sent_at timestamptz,
  password_hash   text NOT NULL,
  first_name      text,
  last_name       text,
  id_role         bigint REFERENCES roles(id) ON DELETE SET NULL,
  mfa_secret      text,
  mfa_enabled     boolean NOT NULL DEFAULT false,
  is_active       boolean NOT NULL DEFAULT true,
  last_login_at   timestamptz,
  created_at      timestamptz NOT NULL DEFAULT now(),
  updated_at      timestamptz NOT NULL DEFAULT now(),
  deleted_at      timestamptz,
  CONSTRAINT uq_users_email UNIQUE (email),
  CONSTRAINT chk_users_email CHECK (char_length(email) > 3)
);

CREATE TABLE roles (
  id          bigserial PRIMARY KEY,
  code        text NOT NULL UNIQUE,
  name        text NOT NULL,
  description text,
  created_at  timestamptz NOT NULL DEFAULT now(),
  updated_at  timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT chk_roles_code CHECK (char_length(code) > 0),
  CONSTRAINT chk_roles_name CHECK (char_length(name) > 0)
);

CREATE TABLE api_keys (
  id             bigserial PRIMARY KEY,
  organizer_id   bigint NOT NULL,
  name           text NOT NULL,
  token_hash     text NOT NULL UNIQUE,
  scopes         text[] NOT NULL DEFAULT '{}',
  created_at     timestamptz NOT NULL DEFAULT now(),
  updated_at     timestamptz NOT NULL DEFAULT now(),
  last_used_at   timestamptz,
  revoked        boolean NOT NULL DEFAULT false,
  CONSTRAINT uq_api_keys_org_name UNIQUE (organizer_id, name),
  CONSTRAINT chk_api_keys_name CHECK (char_length(name) > 0)
);

CREATE TABLE audit_logs (
  id          bigserial PRIMARY KEY,
  user_id     bigint REFERENCES users(id) ON DELETE SET NULL,
  action      text NOT NULL CHECK (char_length(action) > 0),
  metadata    jsonb,
  created_at  timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE password_reset_tokens (
  id          bigserial PRIMARY KEY,
  user_id     bigint NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  token_hash  text NOT NULL UNIQUE,
  expires_at  timestamptz NOT NULL,
  used        boolean DEFAULT false,
  created_at  timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT chk_password_reset_expires CHECK (expires_at > now())
);

CREATE TABLE refresh_tokens (
  id          bigserial PRIMARY KEY,
  user_id     bigint NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  token_hash  text NOT NULL UNIQUE,
  user_agent  text,
  ip_address  text,
  remembered  boolean NOT NULL DEFAULT false,
  revoked     boolean NOT NULL DEFAULT false,
  created_at  timestamptz NOT NULL DEFAULT now(),
  expires_at  timestamptz NOT NULL,
  CONSTRAINT chk_refresh_expires CHECK (expires_at > now())
);

CREATE TABLE login_attempts (
  id          bigserial PRIMARY KEY,
  user_id     bigint REFERENCES users(id) ON DELETE CASCADE,
  email       text,
  success     boolean NOT NULL,
  ip_address  text,
  user_agent  text,
  created_at  timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT chk_login_email CHECK (email IS NULL OR char_length(email) > 3)
);
