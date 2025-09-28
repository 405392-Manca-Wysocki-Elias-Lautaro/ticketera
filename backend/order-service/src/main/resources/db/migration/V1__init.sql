CREATE TABLE customers (
  id          bigserial PRIMARY KEY,
  email       text NOT NULL UNIQUE,
  first_name  text,
  last_name   text,
  phone       text,
  user_id     bigint,
  created_at  timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE orders (
  id            bigserial PRIMARY KEY,
  customer_id   bigint NOT NULL,
  organizer_id  bigint NOT NULL,
  status        text NOT NULL DEFAULT 'pending',
  total_cents   bigint NOT NULL DEFAULT 0,
  currency      text NOT NULL DEFAULT 'ARS',
  created_at    timestamptz NOT NULL DEFAULT now(),
  paid_at       timestamptz
);

ALTER TABLE orders
  ADD CONSTRAINT ck_orders_status
  CHECK (status IN ('pending','paid','failed','canceled','refunded','partially_refunded'));

CREATE TABLE order_items (
  id               bigserial PRIMARY KEY,
  order_id         bigint NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  occurrence_id    bigint NOT NULL,
  venue_area_id    bigint,
  venue_seat_id    bigint,
  ticket_type_id   bigint NOT NULL,
  unit_price_cents bigint NOT NULL,
  quantity         int NOT NULL DEFAULT 1
);

CREATE TABLE order_status_history (
  id          bigserial PRIMARY KEY,
  order_id    bigint NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  from_status text,
  to_status   text NOT NULL,
  changed_by  bigint,
  changed_at  timestamptz NOT NULL DEFAULT now(),
  note        text
);
