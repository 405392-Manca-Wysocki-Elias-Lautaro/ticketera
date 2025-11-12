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

-- Categories
CREATE TABLE events.categories (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  name          text NOT NULL UNIQUE,
  description   text,
  active        boolean NOT NULL DEFAULT true
);

-- Events
CREATE TABLE events.events (
  id                uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  organizer_id      uuid NOT NULL REFERENCES events.organizers(id),
  title             text NOT NULL,
  slug              text NOT NULL UNIQUE,
  description       text,
  category_id       uuid NOT NULL REFERENCES events.categories(id),
  cover_url         text,
  status            text NOT NULL DEFAULT 'draft',
  -- Campos de venue integrados
  venue_name        text NOT NULL,
  venue_description text,
  address_line      text NOT NULL,
  city              text NOT NULL,
  state             text NOT NULL,
  country           text NOT NULL,
  lat               decimal(9,6),
  lng               decimal(9,6),
  -- Campos de occurrence integrados
  starts_at         timestamptz NOT NULL,
  ends_at           timestamptz,
  -- Metadatos
  created_at        timestamptz NOT NULL DEFAULT now(),
  active            boolean NOT NULL DEFAULT true
);

-- Seat Types
CREATE TABLE events.seat_types (
  id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  code        text NOT NULL UNIQUE,
  name        text NOT NULL,
  description text
);

-- Areas
CREATE TABLE events.areas (
  id                   uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  event_id             uuid NOT NULL REFERENCES events.events(id),
  name                 text NOT NULL,
  is_general_admission boolean NOT NULL DEFAULT false,
  capacity             int,
  position             int
);

-- Area Pricing (precios por área)
CREATE TABLE events.area_pricing (
  id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  area_id     uuid NOT NULL REFERENCES events.areas(id) ON DELETE CASCADE,
  price_cents bigint NOT NULL,
  currency    text NOT NULL DEFAULT 'ARS',
  created_at  timestamptz NOT NULL DEFAULT now()
);

-- Seats
CREATE TABLE events.seats (
  id             uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  area_id        uuid NOT NULL REFERENCES events.areas(id),
  seat_number    int NOT NULL,
  row_number     int NOT NULL,
  label          text NOT NULL
);

-- Índices para mejorar el rendimiento
CREATE INDEX idx_areas_event_id ON events.areas(event_id);
CREATE INDEX idx_seats_area_id ON events.seats(area_id);
CREATE INDEX idx_seats_row_number ON events.seats(row_number);
CREATE INDEX idx_seats_seat_number ON events.seats(seat_number);
CREATE INDEX idx_seats_label ON events.seats(label);
CREATE INDEX idx_seats_row_seat ON events.seats(area_id, row_number, seat_number);
