CREATE SCHEMA IF NOT EXISTS tickets;

CREATE TABLE tickets.tickets (
  id             bigserial PRIMARY KEY,
  order_item_id  bigint NOT NULL,
  occurrence_id  bigint NOT NULL,
  event_venue_area_id bigint,
  event_venue_seat_id bigint,
  ticket_type_id bigint NOT NULL,
  code           text NOT NULL UNIQUE,
  status         text NOT NULL DEFAULT 'issued',
  issued_at      timestamptz NOT NULL DEFAULT now(),
  checked_in_at  timestamptz,
  created_at     timestamptz NOT NULL DEFAULT now(),
  updated_at     timestamptz NOT NULL DEFAULT now(),
  deleted_at     timestamptz,
  CONSTRAINT ck_tickets_status CHECK (status IN ('issued','checked_in','canceled','refunded'))
);

CREATE TABLE tickets.ticket_status_history (
  id                bigserial PRIMARY KEY,
  ticket_id         bigint NOT NULL REFERENCES tickets.tickets(id) ON DELETE CASCADE,
  from_status       text,
  to_status         text NOT NULL,
  last_updated_user bigint,
  updated_user      timestamptz NOT NULL DEFAULT now(),
  note              text
);

CREATE TABLE tickets.holds (
  id            bigserial PRIMARY KEY,
  customer_id   bigint,
  occurrence_id bigint NOT NULL,
  event_venue_area_id bigint,
  event_venue_seat_id bigint,
  quantity      int NOT NULL DEFAULT 1,
  expires_at    timestamptz NOT NULL,
  status        text NOT NULL DEFAULT 'active',
  created_at    timestamptz NOT NULL DEFAULT now(),
  updated_at    timestamptz NOT NULL DEFAULT now(),
  deleted_at    timestamptz,
  CONSTRAINT ck_holds_status CHECK (status IN ('active','converted','expired','canceled'))
);
