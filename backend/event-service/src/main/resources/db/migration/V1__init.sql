CREATE SCHEMA IF NOT EXISTS events;

-- Organizers
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

-- Categories
CREATE TABLE events.categories (
  id            bigserial PRIMARY KEY,
  name          text NOT NULL UNIQUE,
  description   text,
  active        boolean NOT NULL DEFAULT true
);

-- Events
CREATE TABLE events.events (
  id            bigserial PRIMARY KEY,
  organizer_id  bigint NOT NULL REFERENCES events.organizers(id),
  title         text NOT NULL,
  slug          text NOT NULL UNIQUE,
  description   text,
  category_id   bigint NOT NULL REFERENCES events.categories(id),
  cover_url     text,
  status        text NOT NULL DEFAULT 'draft',
  created_at    timestamptz NOT NULL DEFAULT now(),
  active        boolean NOT NULL DEFAULT true
);