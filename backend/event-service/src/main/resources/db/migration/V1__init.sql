CREATE SCHEMA IF NOT EXISTS events;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

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
  lat           decimal(9,6) NOT NULL,
  lng           decimal(9,6) NOT NULL,
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

-- Seat Types
CREATE TABLE events.seat_types (
  id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  code        text NOT NULL UNIQUE,
  name        text NOT NULL,
  description text
);

-- Venue Areas
CREATE TABLE events.venue_areas (
  id                   uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  venue_id             uuid NOT NULL REFERENCES events.venues(id),
  name                 text NOT NULL,
  is_general_admission boolean NOT NULL DEFAULT false,
  capacity             int,
  position             int
);

-- Venue Seats
CREATE TABLE events.venue_seats (
  id             uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  venue_area_id  uuid NOT NULL REFERENCES events.venue_areas(id),
  seat_number    int NOT NULL,
  row_number     int NOT NULL,
  label          text NOT NULL
);

-- Índices para mejorar el rendimiento
CREATE INDEX idx_venue_areas_venue_id ON events.venue_areas(venue_id);
CREATE INDEX idx_venue_seats_venue_area_id ON events.venue_seats(venue_area_id);
CREATE INDEX idx_venue_seats_row_number ON events.venue_seats(row_number);
CREATE INDEX idx_venue_seats_seat_number ON events.venue_seats(seat_number);
CREATE INDEX idx_venue_seats_label ON events.venue_seats(label);
CREATE INDEX idx_venue_seats_row_seat ON events.venue_seats(venue_area_id, row_number, seat_number);
