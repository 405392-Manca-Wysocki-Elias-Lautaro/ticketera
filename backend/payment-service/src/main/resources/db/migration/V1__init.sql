CREATE TABLE payment_providers (
  id   bigserial PRIMARY KEY,
  code text NOT NULL UNIQUE,
  name text NOT NULL
);

CREATE TABLE payment_intents (
  id            bigserial PRIMARY KEY,
  order_id      bigint NOT NULL,
  provider_id   bigint NOT NULL REFERENCES payment_providers(id),
  provider_ref  text,
  status        text NOT NULL DEFAULT 'pending',
  amount_cents  bigint NOT NULL,
  currency      text NOT NULL DEFAULT 'ARS',
  created_at    timestamptz NOT NULL DEFAULT now(),
  updated_at    timestamptz NOT NULL DEFAULT now()
);

ALTER TABLE payment_intents
  ADD CONSTRAINT ck_pi_status
  CHECK (status IN ('pending','authorized','captured','failed','canceled'));

CREATE TABLE payment_captures (
  id                bigserial PRIMARY KEY,
  payment_intent_id bigint NOT NULL REFERENCES payment_intents(id) ON DELETE CASCADE,
  amount_cents      bigint NOT NULL,
  captured_at       timestamptz NOT NULL DEFAULT now(),
  provider_capture_ref text
);

CREATE TABLE refunds (
  id                 bigserial PRIMARY KEY,
  order_id           bigint NOT NULL,
  payment_intent_id  bigint,
  status             text NOT NULL DEFAULT 'requested',
  reason             text,
  amount_cents       bigint NOT NULL,
  created_by         bigint,
  created_at         timestamptz NOT NULL DEFAULT now(),
  processed_at       timestamptz,
  provider_ref       text
);

ALTER TABLE refunds
  ADD CONSTRAINT ck_refunds_status
  CHECK (status IN ('requested','processing','succeeded','failed'));

CREATE TABLE refund_items (
  id            bigserial PRIMARY KEY,
  refund_id     bigint NOT NULL REFERENCES refunds(id) ON DELETE CASCADE,
  order_item_id bigint NOT NULL,
  ticket_id     bigint,
  amount_cents  bigint NOT NULL
);
