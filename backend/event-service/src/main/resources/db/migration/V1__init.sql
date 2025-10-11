CREATE SCHEMA IF NOT EXISTS events;

-- Organizers
CREATE TABLE events.organizers (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  name          text NOT NULL,
  slug          text NOT NULL UNIQUE,
  contact_email text NOT NULL,
  phone_number  text NOT NULL,
  active        boolean NOT NULL DEFAULT true,
  created_at    timestamptz NOT NULL DEFAULT now()
);

-- Venues
CREATE TABLE events.venues (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  organizer_id  uuid NOT NULL REFERENCES events.organizers(id),
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
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  name          text NOT NULL UNIQUE,
  description   text,
  active        boolean NOT NULL DEFAULT true
);

-- Events
CREATE TABLE events.events (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  organizer_id  uuid NOT NULL REFERENCES events.organizers(id),
  title         text NOT NULL,
  slug          text NOT NULL UNIQUE,
  description   text,
  category_id   uuid NOT NULL REFERENCES events.categories(id),
  cover_url     text,
  status        text NOT NULL DEFAULT 'draft',
  created_at    timestamptz NOT NULL DEFAULT now(),
  active        boolean NOT NULL DEFAULT true
);

-- Occurrences
CREATE TABLE events.occurrences (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  event_id      uuid NOT NULL REFERENCES events.events(id),
  venue_id      uuid NOT NULL REFERENCES events.venues(id),
  starts_at     timestamptz NOT NULL,
  ends_at       timestamptz NOT NULL,
  status        text NOT NULL DEFAULT 'draft',
  slug          text NOT NULL UNIQUE,
  created_at    timestamptz NOT NULL DEFAULT now(),
  active        boolean NOT NULL DEFAULT true
);
