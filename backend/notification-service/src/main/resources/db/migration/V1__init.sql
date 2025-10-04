CREATE SCHEMA IF NOT EXISTS notifications;

CREATE TABLE notifications.tickets_outbox (
  id             bigserial PRIMARY KEY,
  aggregate_type text NOT NULL,
  aggregate_id   bigint NOT NULL,
  event_type     text NOT NULL,
  payload        jsonb NOT NULL,
  occurred_at    timestamptz NOT NULL DEFAULT now(),
  published_at   timestamptz
);

CREATE TABLE notifications.payments_outbox (
  id             bigserial PRIMARY KEY,
  aggregate_type text NOT NULL,
  aggregate_id   bigint NOT NULL,
  event_type     text NOT NULL,
  payload        jsonb NOT NULL,
  occurred_at    timestamptz NOT NULL DEFAULT now(),
  published_at   timestamptz
);
