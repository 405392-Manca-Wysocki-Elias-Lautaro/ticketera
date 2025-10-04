CREATE SCHEMA IF NOT EXISTS orders;

CREATE TABLE orders.customers (
  id        bigserial PRIMARY KEY,
  email     text NOT NULL UNIQUE,
  first_name text,
  last_name  text,
  phone      text,
  user_id    bigint REFERENCES auth.users(id),
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  deleted_at timestamptz,
  UNIQUE (user_id) WHERE user_id IS NOT NULL
);

CREATE TABLE orders.orders (
  id           bigserial PRIMARY KEY,
  customer_id  bigint NOT NULL REFERENCES orders.customers(id),
  organizer_id bigint NOT NULL REFERENCES auth.organizers(id),
  status       text NOT NULL DEFAULT 'pending',
  total_cents  bigint NOT NULL DEFAULT 0,
  currency     text NOT NULL DEFAULT 'ARS',
  created_at   timestamptz NOT NULL DEFAULT now(),
  updated_at   timestamptz NOT NULL DEFAULT now(),
  deleted_at   timestamptz,
  paid_at      timestamptz,
  CONSTRAINT ck_orders_status CHECK (status IN ('pending','paid','failed','canceled','refunded','partially_refunded'))
);

CREATE TABLE orders.order_items (
  id              bigserial PRIMARY KEY,
  order_id        bigint NOT NULL REFERENCES orders.orders(id) ON DELETE CASCADE,
  occurrence_id   bigint NOT NULL REFERENCES events.event_occurrences(id),
  event_venue_area_id bigint REFERENCES events.event_venue_areas(id),
  event_venue_seat_id bigint REFERENCES events.event_venue_seats(id),
  ticket_type_id  bigint NOT NULL REFERENCES events.ticket_types(id),
  unit_price_cents bigint NOT NULL,
  quantity         int NOT NULL DEFAULT 1,
  created_at       timestamptz NOT NULL DEFAULT now(),
  updated_at       timestamptz NOT NULL DEFAULT now(),
  deleted_at       timestamptz,
  UNIQUE (order_id, event_venue_seat_id),
  CHECK ((event_venue_seat_id IS NULL) = (quantity > 0))
);

CREATE TABLE orders.order_status_history (
  id          bigserial PRIMARY KEY,
  order_id    bigint NOT NULL REFERENCES orders.orders(id) ON DELETE CASCADE,
  from_status text,
  to_status   text NOT NULL,
  changed_by  bigint REFERENCES auth.users(id),
  changed_at  timestamptz NOT NULL DEFAULT now(),
  note        text
);
