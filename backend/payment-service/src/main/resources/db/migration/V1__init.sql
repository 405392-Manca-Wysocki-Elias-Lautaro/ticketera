CREATE SCHEMA IF NOT EXISTS payments;

CREATE TABLE payments.payment_providers (
  id          bigserial PRIMARY KEY,
  code        text NOT NULL UNIQUE,
  name        text NOT NULL,
  created_at  timestamptz NOT NULL DEFAULT now(),
  updated_at  timestamptz NOT NULL DEFAULT now(),
  deleted_at  timestamptz
);

CREATE TABLE payments.payment_intents (
  id           bigserial PRIMARY KEY,
  order_id     bigint NOT NULL REFERENCES orders.orders(id) ON DELETE CASCADE,
  provider_id  bigint NOT NULL REFERENCES payments.payment_providers(id),
  provider_ref text,
  status       text NOT NULL DEFAULT 'pending',
  amount_cents bigint NOT NULL,
  currency     text NOT NULL DEFAULT 'ARS',
  created_at   timestamptz NOT NULL DEFAULT now(),
  updated_at   timestamptz NOT NULL DEFAULT now(),
  deleted_at   timestamptz,
  CONSTRAINT ck_pi_status CHECK (status IN ('pending','authorized','captured','failed','canceled'))
);

CREATE TABLE payments.payment_captures (
  id                  bigserial PRIMARY KEY,
  payment_intent_id   bigint NOT NULL REFERENCES payments.payment_intents(id) ON DELETE CASCADE,
  amount_cents        bigint NOT NULL,
  captured_at         timestamptz NOT NULL DEFAULT now(),
  provider_capture_ref text
);

CREATE TABLE payments.refunds (
  id            bigserial PRIMARY KEY,
  order_id      bigint NOT NULL REFERENCES orders.orders(id) ON DELETE CASCADE,
  payment_intent_id bigint REFERENCES payments.payment_intents(id),
  status        text NOT NULL DEFAULT 'requested',
  reason        text,
  amount_cents  bigint NOT NULL,
  created_by    bigint REFERENCES auth.users(id),
  created_at    timestamptz NOT NULL DEFAULT now(),
  updated_at    timestamptz NOT NULL DEFAULT now(),
  deleted_at    timestamptz,
  processed_at  timestamptz,
  provider_ref  text,
  CONSTRAINT ck_refunds_status CHECK (status IN ('requested','processing','succeeded','failed'))
);

CREATE TABLE payments.refund_items (
  id            bigserial PRIMARY KEY,
  refund_id     bigint NOT NULL REFERENCES payments.refunds(id) ON DELETE CASCADE,
  order_item_id bigint NOT NULL REFERENCES orders.order_items(id) ON DELETE CASCADE,
  ticket_id     bigint REFERENCES tickets.tickets(id),
  amount_cents  bigint NOT NULL
);

CREATE TABLE payments.refund_policies (
  id           bigserial PRIMARY KEY,
  organizer_id bigint REFERENCES auth.organizers(id) ON DELETE CASCADE,
  event_id     bigint REFERENCES events.events(id) ON DELETE CASCADE,
  policy_json  jsonb NOT NULL,
  created_at   timestamptz NOT NULL DEFAULT now(),
  updated_at   timestamptz NOT NULL DEFAULT now(),
  deleted_at   timestamptz,
  UNIQUE (organizer_id, event_id)
);
