-- =========================================================
-- Migración V6: Sistema de Cupones y Promociones
-- =========================================================
-- Fecha: 2026-02-08
-- Descripción: Agrega tablas para gestión de cupones/promociones
--              con validaciones, límites y sistema de reportes
-- =========================================================

-- ========================================
-- TABLA: orders.coupons
-- ========================================
CREATE TABLE IF NOT EXISTS orders.coupons (
  id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  organizer_id             UUID NOT NULL, -- FK lógica a events.organizers
  code                     TEXT NOT NULL,
  description              TEXT,
  
  -- Tipo de descuento
  discount_type            TEXT NOT NULL CHECK (discount_type IN ('PERCENTAGE', 'FIXED_AMOUNT')),
  discount_value           BIGINT NOT NULL CHECK (discount_value > 0),
  currency                 TEXT NOT NULL DEFAULT 'ARS',
  
  -- Límites de uso
  max_uses                 INTEGER CHECK (max_uses IS NULL OR max_uses > 0),
  max_uses_per_customer    INTEGER CHECK (max_uses_per_customer IS NULL OR max_uses_per_customer > 0),
  current_uses             INTEGER NOT NULL DEFAULT 0,
  
  -- Vigencia
  valid_from               TIMESTAMPTZ NOT NULL,
  valid_until              TIMESTAMPTZ NOT NULL,
  
  -- Restricciones
  event_ids                UUID[], -- Array de UUIDs de eventos permitidos (vacío = todos)
  min_purchase_amount_cents BIGINT CHECK (min_purchase_amount_cents IS NULL OR min_purchase_amount_cents >= 0),
  
  -- Estado
  status                   TEXT NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'EXPIRED', 'EXHAUSTED')),
  
  -- Auditoría
  created_at               TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at               TIMESTAMPTZ NOT NULL DEFAULT now(),
  deleted_at               TIMESTAMPTZ,
  created_by               UUID, -- Usuario que creó el cupón
  
  -- Constraints
  CONSTRAINT ck_valid_dates CHECK (valid_from < valid_until),
  CONSTRAINT ck_currency_for_fixed CHECK (
    discount_type = 'PERCENTAGE' OR 
    (discount_type = 'FIXED_AMOUNT' AND currency IS NOT NULL)
  )
);

-- Índice único para código por organizador (case-insensitive, ignorando soft deletes)
CREATE UNIQUE INDEX uq_coupon_code_organizer 
ON orders.coupons(organizer_id, UPPER(code)) 
WHERE deleted_at IS NULL;

-- Índices de performance
CREATE INDEX idx_coupons_organizer ON orders.coupons(organizer_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_coupons_code ON orders.coupons(UPPER(code)) WHERE deleted_at IS NULL;
CREATE INDEX idx_coupons_status ON orders.coupons(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_coupons_valid_dates ON orders.coupons(valid_from, valid_until) WHERE deleted_at IS NULL;
CREATE INDEX idx_coupons_events ON orders.coupons USING GIN(event_ids); -- Para búsquedas en array

-- ========================================
-- TABLA: orders.coupon_redemptions
-- ========================================
CREATE TABLE IF NOT EXISTS orders.coupon_redemptions (
  id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  coupon_id         UUID NOT NULL REFERENCES orders.coupons(id),
  order_id          UUID NOT NULL REFERENCES orders.orders(id),
  customer_id       UUID NOT NULL REFERENCES orders.customers(id),
  
  -- Datos de la redención
  discount_applied_cents BIGINT NOT NULL,
  subtotal_cents         BIGINT NOT NULL,
  
  -- Auditoría
  redeemed_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  
  -- Constraints: Una orden puede tener solo 1 cupón
  CONSTRAINT uq_redemption_order UNIQUE (order_id)
);

-- Índices para reportes y queries de redenciones
CREATE INDEX idx_redemptions_coupon ON orders.coupon_redemptions(coupon_id);
CREATE INDEX idx_redemptions_customer ON orders.coupon_redemptions(customer_id);
CREATE INDEX idx_redemptions_order ON orders.coupon_redemptions(order_id);
CREATE INDEX idx_redemptions_date ON orders.coupon_redemptions(redeemed_at);

-- ========================================
-- MODIFICAR TABLA: orders.orders
-- ========================================
-- Agregar campos para tracking de cupones
ALTER TABLE orders.orders ADD COLUMN IF NOT EXISTS coupon_id UUID REFERENCES orders.coupons(id);
ALTER TABLE orders.orders ADD COLUMN IF NOT EXISTS discount_amount_cents BIGINT DEFAULT 0;

-- Constraints de validación (solo si no existe ya)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint 
    WHERE conname = 'ck_discount_not_negative' 
    AND conrelid = 'orders.orders'::regclass
  ) THEN
    ALTER TABLE orders.orders ADD CONSTRAINT ck_discount_not_negative 
      CHECK (discount_amount_cents >= 0);
  END IF;
END $$;

-- Índice para búsquedas por cupón usado
CREATE INDEX IF NOT EXISTS idx_orders_coupon_id ON orders.orders(coupon_id) 
  WHERE coupon_id IS NOT NULL;

-- ========================================
-- FUNCIONES Y TRIGGERS
-- ========================================

-- Función para actualizar estado de cupón automáticamente
CREATE OR REPLACE FUNCTION orders.update_coupon_status()
RETURNS TRIGGER AS $$
BEGIN
    -- Marcar como EXHAUSTED si alcanzó max_uses
    IF NEW.current_uses >= NEW.max_uses AND NEW.max_uses IS NOT NULL THEN
        NEW.status = 'EXHAUSTED';
    END IF;
    
    -- Marcar como EXPIRED si pasó valid_until
    IF NEW.valid_until < NOW() AND NEW.status = 'ACTIVE' THEN
        NEW.status = 'EXPIRED';
    END IF;
    
    -- Actualizar updated_at
    NEW.updated_at = NOW();
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger para actualizar estado automáticamente
CREATE TRIGGER trg_update_coupon_status
BEFORE UPDATE ON orders.coupons
FOR EACH ROW
EXECUTE FUNCTION orders.update_coupon_status();

-- ========================================
-- COMENTARIOS DE DOCUMENTACIÓN
-- ========================================
COMMENT ON TABLE orders.coupons IS 'Cupones de descuento creados por organizadores para promociones';
COMMENT ON TABLE orders.coupon_redemptions IS 'Registro histórico de uso de cupones en órdenes';

COMMENT ON COLUMN orders.coupons.code IS 'Código único alfanumérico del cupón (case-insensitive)';
COMMENT ON COLUMN orders.coupons.discount_type IS 'PERCENTAGE: descuento porcentual (1-100), FIXED_AMOUNT: monto fijo en centavos';
COMMENT ON COLUMN orders.coupons.event_ids IS 'Array de UUIDs de eventos donde aplica. Vacío o NULL = todos los eventos del organizador';
COMMENT ON COLUMN orders.coupons.status IS 'ACTIVE: usable, INACTIVE: desactivado, EXPIRED: vencido, EXHAUSTED: alcanzó límite';
COMMENT ON COLUMN orders.coupons.current_uses IS 'Contador de veces que el cupón ha sido usado exitosamente';

COMMENT ON COLUMN orders.orders.discount_amount_cents IS 'Monto descontado por cupón en centavos';
COMMENT ON COLUMN orders.orders.coupon_id IS 'Referencia al cupón aplicado (si existe)';

-- ========================================
-- FIN DE MIGRACIÓN V6
-- ========================================

