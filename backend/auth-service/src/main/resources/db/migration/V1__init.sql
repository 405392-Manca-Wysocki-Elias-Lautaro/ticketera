CREATE TABLE users (
  id            bigserial PRIMARY KEY,
  email         text NOT NULL UNIQUE,
  password_hash text NOT NULL,
  first_name    text,
  last_name     text,
  is_active     boolean NOT NULL DEFAULT true,
  created_at    timestamptz NOT NULL DEFAULT now(),
  last_login_at timestamptz
);

CREATE TABLE roles (
  id    bigserial PRIMARY KEY,
  code  text NOT NULL UNIQUE,  -- 'owner','admin','manager','seller','checker','viewer'
  name  text NOT NULL,
  description text
);

CREATE TABLE api_keys (
  id             bigserial PRIMARY KEY,
  organizer_id   bigint NOT NULL,
  name           text NOT NULL,
  token_hash     text NOT NULL,
  scopes         text[] NOT NULL DEFAULT '{}',
  created_at     timestamptz NOT NULL DEFAULT now(),
  last_used_at   timestamptz
);
