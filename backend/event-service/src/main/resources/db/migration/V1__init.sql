CREATE SCHEMA IF NOT EXISTS events;

CREATE TABLE events.events (
  id            bigserial PRIMARY KEY,
  organizer_id  bigint NOT NULL REFERENCES auth.organizers(id),
  title         text NOT NULL,
  slug          text NOT NULL,
  description   text,
  category_id   bigint REFERENCES auth.categories(id),
  cover_url     text,
  status        text NOT NULL DEFAULT 'draft',
  created_at    timestamptz NOT NULL DEFAULT now(),
  updated_at    timestamptz NOT NULL DEFAULT now(),
  deleted_at    timestamptz,
  UNIQUE (organizer_id, slug),
  CONSTRAINT ck_events_status CHECK (status IN ('draft','published','archived'))
);

CREATE TABLE events.event_grants (
  id            bigserial PRIMARY KEY,
  event_id      bigint NOT NULL REFERENCES events.events(id) ON DELETE CASCADE,
  membership_id bigint NOT NULL REFERENCES auth.organizer_memberships(id) ON DELETE CASCADE,
  can_manage    boolean NOT NULL DEFAULT false,
  can_sell      boolean NOT NULL DEFAULT false,
  can_check     boolean NOT NULL DEFAULT false,
  created_at    timestamptz NOT NULL DEFAULT now(),
  updated_at    timestamptz NOT NULL DEFAULT now(),
  deleted_at    timestamptz,
  UNIQUE (event_id, membership_id)
);

CREATE TABLE events.event_media (
  id        bigserial PRIMARY KEY,
  event_id  bigint NOT NULL REFERENCES events.events(id) ON DELETE CASCADE,
  url       text NOT NULL,
  alt       text,
  position  int NOT NULL DEFAULT 0,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  deleted_at timestamptz
);

-- VENUE base (plantilla)
CREATE TABLE events.venues (
  id            bigserial PRIMARY KEY,
  organizer_id  bigint REFERENCES auth.organizers(id),
  name          text NOT NULL,
  description   text,
  address_line  text,
  city          text,
  state         text,
  country       text,
  lat           numeric(10,6),
  lng           numeric(10,6),
  created_at    timestamptz NOT NULL DEFAULT now(),
  updated_at    timestamptz NOT NULL DEFAULT now(),
  deleted_at    timestamptz
);

-- FUNCIONES
CREATE TABLE events.event_occurrences (
  id        bigserial PRIMARY KEY,
  event_id  bigint NOT NULL REFERENCES events.events(id) ON DELETE CASCADE,
  venue_id  bigint NOT NULL REFERENCES events.venues(id),
  starts_at timestamptz NOT NULL,
  ends_at   timestamptz,
  status    text NOT NULL DEFAULT 'scheduled',
  slug      text,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  deleted_at timestamptz,
  UNIQUE (event_id, starts_at),
  CONSTRAINT ck_occ_status CHECK (status IN ('scheduled','on_sale','closed','canceled'))
);

-- ÁREAS y ASIENTOS por evento/función
CREATE TABLE events.event_venue_areas (
  id            bigserial PRIMARY KEY,
  occurrence_id bigint NOT NULL REFERENCES events.event_occurrences(id) ON DELETE CASCADE,
  name          text NOT NULL,
  is_general_admission boolean NOT NULL DEFAULT false,
  capacity      int,
  position      int NOT NULL DEFAULT 0,
  created_at    timestamptz NOT NULL DEFAULT now(),
  updated_at    timestamptz NOT NULL DEFAULT now(),
  deleted_at    timestamptz,
  CONSTRAINT ck_event_venue_areas_ga_capacity CHECK (
    (is_general_admission AND capacity IS NOT NULL)
    OR (NOT is_general_admission AND capacity IS NULL)
  )
);

CREATE TABLE events.event_venue_seats (
  id                  bigserial PRIMARY KEY,
  event_venue_area_id bigint NOT NULL REFERENCES events.event_venue_areas(id) ON DELETE CASCADE,
  seat_label          text NOT NULL,
  row_label           text,
  number_label        text,
  created_at          timestamptz NOT NULL DEFAULT now(),
  updated_at          timestamptz NOT NULL DEFAULT now(),
  deleted_at          timestamptz,
  UNIQUE (event_venue_area_id, seat_label)
);

-- Tipos de asiento y tickets
CREATE TABLE events.seat_types (
  id          bigserial PRIMARY KEY,
  code        text NOT NULL UNIQUE,
  name        text NOT NULL,
  description text,
  created_at  timestamptz NOT NULL DEFAULT now(),
  updated_at  timestamptz NOT NULL DEFAULT now(),
  deleted_at  timestamptz
);

CREATE TABLE events.ticket_types (
  id          bigserial PRIMARY KEY,
  code        text NOT NULL UNIQUE,
  name        text NOT NULL,
  description text,
  created_at  timestamptz NOT NULL DEFAULT now(),
  updated_at  timestamptz NOT NULL DEFAULT now(),
  deleted_at  timestamptz
);

-- Inventario GA por occurrence/área
CREATE TABLE events.area_allocations (
  id              bigserial PRIMARY KEY,
  occurrence_id   bigint NOT NULL REFERENCES events.event_occurrences(id) ON DELETE CASCADE,
  event_venue_area_id bigint NOT NULL REFERENCES events.event_venue_areas(id),
  capacity        int NOT NULL,
  sold            int NOT NULL DEFAULT 0,
  created_at      timestamptz NOT NULL DEFAULT now(),
  updated_at      timestamptz NOT NULL DEFAULT now(),
  deleted_at      timestamptz,
  UNIQUE (occurrence_id, event_venue_area_id),
  CONSTRAINT ck_alloc_not_exceed CHECK (sold <= capacity)
);

-- Precios
CREATE TABLE events.prices (
  id             bigserial PRIMARY KEY,
  occurrence_id  bigint NOT NULL REFERENCES events.event_occurrences(id) ON DELETE CASCADE,
  ticket_type_id bigint NOT NULL REFERENCES events.ticket_types(id),
  seat_type_id   bigint REFERENCES events.seat_types(id),
  event_venue_area_id bigint REFERENCES events.event_venue_areas(id),
  event_venue_seat_id bigint REFERENCES events.event_venue_seats(id),
  amount_cents   bigint NOT NULL,
  currency       text NOT NULL DEFAULT 'ARS',
  starts_at      timestamptz,
  ends_at        timestamptz,
  created_at     timestamptz NOT NULL DEFAULT now(),
  updated_at     timestamptz NOT NULL DEFAULT now(),
  deleted_at     timestamptz,
  CHECK (
    (seat_type_id IS NOT NULL)::int +
    (event_venue_area_id IS NOT NULL)::int +
    (event_venue_seat_id IS NOT NULL)::int = 1
  )
);
