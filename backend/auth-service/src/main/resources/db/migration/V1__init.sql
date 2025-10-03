CREATE TABLE users (
  id              bigserial PRIMARY KEY,
  email           text NOT NULL UNIQUE,
  email_verified  boolean NOT NULL DEFAULT false,
  password_hash   text NOT NULL,
  first_name      text,
  last_name       text,
  mfa_secret      text,
  mfa_enabled     boolean NOT NULL DEFAULT false,
  is_active       boolean NOT NULL DEFAULT true,
  last_login_at   timestamptz
  created_at      timestamptz NOT NULL DEFAULT now(),
  updated_at      timestamptz NOT NULL DEFAULT now()
  deleted_at      timestamptz
);

CREATE TABLE roles (
  id          bigserial PRIMARY KEY,
  code        text NOT NULL UNIQUE,
  name        text NOT NULL,
  description text,
  created_at  timestamptz NOT NULL DEFAULT now(),
  updated_at  timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE user_roles (
  user_id      bigint NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  role_id      bigint NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
  organizer_id bigint,
  PRIMARY KEY(user_id, role_id, organizer_id)
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
  CONSTRAINT uq_api_keys_org_name UNIQUE (organizer_id, name)
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
  created_at  timestamptz NOT NULL DEFAULT now()
);
