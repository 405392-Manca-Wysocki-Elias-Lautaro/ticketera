CREATE TABLE organizers (
  id               bigserial PRIMARY KEY,
  name             text NOT NULL,
  slug             text NOT NULL UNIQUE,
  contact_email    text,
  created_at       timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE categories (
  id               bigserial PRIMARY KEY,
  name             text NOT NULL UNIQUE
);

CREATE TABLE events (
  id               bigserial PRIMARY KEY,
  organizer_id     bigint NOT NULL REFERENCES organizers(id),
  title            text NOT NULL,
  slug             text NOT NULL,
  description      text,
  category_id      bigint REFERENCES categories(id),
  cover_url        text,
  status           text NOT NULL DEFAULT 'draft',
  created_at       timestamptz NOT NULL DEFAULT now(),
  UNIQUE (organizer_id, slug)
);

ALTER TABLE events
  ADD CONSTRAINT ck_events_status CHECK (status IN ('draft','published','archived'));

CREATE TABLE event_media (
  id       bigserial PRIMARY KEY,
  event_id bigint NOT NULL REFERENCES events(id) ON DELETE CASCADE,
  url      text NOT NULL,
  alt      text,
  position int NOT NULL DEFAULT 0
);

CREATE TABLE venues (
  id           bigserial PRIMARY KEY,
  organizer_id bigint REFERENCES organizers(id),
  name         text NOT NULL,
  description  text,
  address_line text,
  city         text,
  state        text,
  country      text,
  lat          numeric(10,6),
  lng          numeric(10,6),
  created_at   timestamptz NOT NULL DEFAULT now()
);

-- venue_areas, venue_seats, seat_types, area_seat_type_rules,
-- ticket_types, event_occurrences, area_allocations, prices
-- (todos los de gestión de venue, fechas y pricing)
