CREATE SCHEMA IF NOT EXISTS orders;

CREATE TABLE orders.customers (
  id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email     text NOT NULL UNIQUE,
  first_name text,
  last_name  text,
  phone      text,
  user_id    bigint,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  deleted_at timestamptz
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_customers_user_id_not_null
  ON orders.customers(user_id)
  WHERE user_id IS NOT NULL;

CREATE TABLE orders.orders (
  id           UUID PRIMARY KEY,
  customer_id  UUID NOT NULL REFERENCES orders.customers(id),
  organizer_id UUID NOT NULL,
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
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id        UUID NOT NULL REFERENCES orders.orders(id) ON DELETE CASCADE,
  occurrence_id   UUID NOT NULL,
  event_venue_area_id UUID,
  event_venue_seat_id UUID,
  ticket_type_id  UUID NOT NULL,
  unit_price_cents bigint NOT NULL,
  quantity         int NOT NULL DEFAULT 1,
  created_at       timestamptz NOT NULL DEFAULT now(),
  updated_at       timestamptz NOT NULL DEFAULT now(),
  deleted_at       timestamptz,
  UNIQUE (order_id, event_venue_seat_id),
  CHECK ((event_venue_seat_id IS NULL) = (quantity > 0))
);

CREATE TABLE orders.order_status_history (
  id          UUID PRIMARY KEY,
  order_id    UUID NOT NULL REFERENCES orders.orders(id) ON DELETE CASCADE,
  from_status text,
  to_status   text NOT NULL,
  changed_by  UUID,
  changed_at  timestamptz NOT NULL DEFAULT now(),
  note        text
);
