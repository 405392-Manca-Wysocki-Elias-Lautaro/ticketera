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

-- Venues
CREATE TABLE events.venues (
  id            bigserial PRIMARY KEY,
  organizer_id  bigint NOT NULL REFERENCES events.organizers(id),
  name          text NOT NULL,
  description   text NOT NULL,
  address_line  text NOT NULL,
  city          text NOT NULL,
  state         text NOT NULL,
  country       text NOT NULL,
  lat           numeric(10,8) NOT NULL,
  lng           numeric(11,8) NOT NULL,
  active        boolean NOT NULL DEFAULT true,
  created_at    timestamptz NOT NULL DEFAULT now()
);