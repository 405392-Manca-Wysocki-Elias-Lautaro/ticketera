-- =====================================================
-- 🎟️ MÓDULO DE TICKETS - INIT SCRIPT
-- =====================================================

-- Crear schema si no existe
CREATE SCHEMA IF NOT EXISTS tickets;

CREATE EXTENSION IF NOT EXISTS "pgcrypto";
-- =====================================================
-- 🧾 Tabla principal: tickets
-- =====================================================
CREATE TABLE IF NOT EXISTS tickets.tickets (
  id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_item_id       UUID NOT NULL,
  event_id            UUID NOT NULL,
  event_venue_area_id UUID,
  event_venue_seat_id UUID,
  user_id             UUID NOT NULL,

  -- Identificadores únicos
  code                text NOT NULL UNIQUE,      -- Código visible / manual
  qr_token            text UNIQUE,               -- Token QR o UUID (nuevo campo)

  -- Precio
  price               numeric(12,2),
  currency            text,
  discount            numeric(12,2),
  final_price         numeric(12,2),
  
  -- Estado y fechas
  status              text NOT NULL DEFAULT 'ISSUED',
  issued_at           timestamptz NOT NULL DEFAULT now(),
  checked_in_at       timestamptz,
  canceled_at         timestamptz,               -- Nueva fecha opcional
  refunded_at         timestamptz,               -- Nueva fecha opcional
  expires_at          timestamptz NOT NULL,
  
  -- Auditoría
  created_user        UUID,
  updated_user        UUID,
  created_at          timestamptz NOT NULL DEFAULT now(),
  updated_at          timestamptz NOT NULL DEFAULT now(),
  deleted_at          timestamptz,

  -- Restricciones de estado
  CONSTRAINT ck_tickets_status CHECK (status IN ('ISSUED','CHECKED_IN','CANCELED','REFUNDED'))
);

-- =====================================================
-- 📜 Historial de estados: ticket_status_history
-- =====================================================
CREATE TABLE IF NOT EXISTS tickets.ticket_status_history (
  id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  ticket_id         UUID NOT NULL REFERENCES tickets.tickets(id) ON DELETE CASCADE,

  from_status       text,
  to_status         text NOT NULL,
  updated_user      UUID,
  updated_at        timestamptz NOT NULL DEFAULT now(),
  note              text
);

-- Índice para búsquedas rápidas
CREATE INDEX IF NOT EXISTS idx_ticket_history_ticket_id ON tickets.ticket_status_history(ticket_id);

-- =====================================================
-- 🕒 Reservas temporales: holds
-- =====================================================
CREATE TABLE IF NOT EXISTS tickets.holds (
  id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  customer_id         UUID,
  event_id            UUID NOT NULL,
  event_venue_area_id UUID,
  event_venue_seat_id UUID,
  quantity            int NOT NULL DEFAULT 1,

  -- Estado y expiración
  expires_at          timestamptz NOT NULL,
  status              text NOT NULL DEFAULT 'ACTIVE',

  -- Relación con ticket final
  converted_ticket_id UUID REFERENCES tickets.tickets(id) ON DELETE SET NULL,
  converted_at        timestamptz,

  -- Auditoría
  created_user        UUID,
  updated_user        UUID,
  created_at          timestamptz NOT NULL DEFAULT now(),
  updated_at          timestamptz NOT NULL DEFAULT now(),
  deleted_at          timestamptz,

  -- Restricciones de estado
  CONSTRAINT ck_holds_status CHECK (status IN ('ACTIVE','CONVERTED','EXPIRED','CANCELED'))
);

-- Índices útiles
CREATE INDEX IF NOT EXISTS idx_holds_event_id ON tickets.holds(event_id);
CREATE INDEX IF NOT EXISTS idx_holds_status ON tickets.holds(status);
CREATE INDEX IF NOT EXISTS idx_tickets_status ON tickets.tickets(status);
CREATE INDEX IF NOT EXISTS idx_tickets_event_id ON tickets.tickets(event_id);
