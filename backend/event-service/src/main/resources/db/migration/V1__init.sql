CREATE SCHEMA IF NOT EXISTS events;

-- Organizadores
CREATE TABLE events.organizers (
  id            bigserial PRIMARY KEY,
  name          text NOT NULL,
  slug          text NOT NULL UNIQUE,
  contact_email text NOT NULL,
  phone_number  text NOT NULL,
  active        boolean NOT NULL DEFAULT true,
  created_at    timestamptz NOT NULL DEFAULT now()
);