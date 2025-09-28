CREATE TABLE tickets (
  id               bigserial PRIMARY KEY,
  order_item_id    bigint NOT NULL,
  occurrence_id    bigint NOT NULL,
  venue_area_id    bigint,
  venue_seat_id    bigint,
  ticket_type_id   bigint NOT NULL,
  code             text NOT NULL UNIQUE,
  status           text NOT NULL DEFAULT 'issued',
  issued_at        timestamptz NOT NULL DEFAULT now(),
  checked_in_at    timestamptz
);

ALTER TABLE tickets
  ADD CONSTRAINT ck_tickets_status
  CHECK (status IN ('issued','checked_in','canceled','refunded'));

CREATE TABLE ticket_status_history (
  id           bigserial PRIMARY KEY,
  ticket_id    bigint NOT NULL REFERENCES tickets(id) ON DELETE CASCADE,
  from_status  text,
  to_status    text NOT NULL,
  changed_by   bigint,
  changed_at   timestamptz NOT NULL DEFAULT now(),
  note         text
);

CREATE TABLE holds (
  id            bigserial PRIMARY KEY,
  customer_id   bigint,
  occurrence_id bigint NOT NULL,
  venue_area_id bigint,
  venue_seat_id bigint,
  quantity      int NOT NULL DEFAULT 1,
  expires_at    timestamptz NOT NULL,
  status        text NOT NULL DEFAULT 'active'
);

CREATE UNIQUE INDEX uq_holds_seat_active
  ON holds(occurrence_id, venue_seat_id)
  WHERE venue_seat_id IS NOT NULL AND status = 'active';
