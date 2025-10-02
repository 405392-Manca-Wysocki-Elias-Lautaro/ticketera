CREATE SCHEMA IF NOT EXISTS auth;

CREATE TABLE auth.users (
  id            bigserial PRIMARY KEY,
  email         text NOT NULL UNIQUE,
  password_hash text NOT NULL,
  first_name    text,
  last_name     text,
  is_active     boolean NOT NULL DEFAULT true,
  created_at    timestamptz NOT NULL DEFAULT now(),
  updated_at    timestamptz NOT NULL DEFAULT now(),
  deleted_at    timestamptz,
  last_login_at timestamptz
);

CREATE TABLE auth.roles (
  id    bigserial PRIMARY KEY,
  code  text NOT NULL UNIQUE,
  name  text NOT NULL,
  description text,
  created_at    timestamptz NOT NULL DEFAULT now(),
  updated_at    timestamptz NOT NULL DEFAULT now(),
  deleted_at    timestamptz
);

CREATE TABLE auth.organizers (
  id            bigserial PRIMARY KEY,
  name          text NOT NULL,
  slug          text NOT NULL UNIQUE,
  contact_email text,
  created_at    timestamptz NOT NULL DEFAULT now(),
  updated_at    timestamptz NOT NULL DEFAULT now(),
  deleted_at    timestamptz
);

CREATE TABLE auth.categories (
  id          bigserial PRIMARY KEY,
  name        text NOT NULL UNIQUE,
  created_at  timestamptz NOT NULL DEFAULT now(),
  updated_at  timestamptz NOT NULL DEFAULT now(),
  deleted_at  timestamptz
);

CREATE TABLE auth.organizer_memberships (
  id            bigserial PRIMARY KEY,
  organizer_id  bigint NOT NULL REFERENCES auth.organizers(id) ON DELETE CASCADE,
  user_id       bigint NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  role_id       bigint NOT NULL REFERENCES auth.roles(id),
  created_at    timestamptz NOT NULL DEFAULT now(),
  updated_at    timestamptz NOT NULL DEFAULT now(),
  deleted_at    timestamptz,
  UNIQUE (organizer_id, user_id)
);

CREATE TABLE auth.api_keys (
  id            bigserial PRIMARY KEY,
  organizer_id  bigint NOT NULL REFERENCES auth.organizers(id) ON DELETE CASCADE,
  name          text NOT NULL,
  token_hash    text NOT NULL,
  scopes        text[] NOT NULL DEFAULT '{}',
  created_at    timestamptz NOT NULL DEFAULT now(),
  updated_at    timestamptz NOT NULL DEFAULT now(),
  deleted_at    timestamptz,
  last_used_at  timestamptz
);
