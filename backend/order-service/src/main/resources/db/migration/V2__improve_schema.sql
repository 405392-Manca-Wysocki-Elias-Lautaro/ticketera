-- Mejoras al esquema de órdenes basadas en el análisis
-- Agregar campos faltantes críticos

-- Mejorar tabla orders
ALTER TABLE orders.orders 
ADD COLUMN expires_at timestamptz,
ADD COLUMN payment_method text,
ADD COLUMN notes text,
ADD COLUMN external_reference text;

-- Agregar constraints mejorados
ALTER TABLE orders.orders 
ADD CONSTRAINT ck_positive_total CHECK (total_cents >= 0),
ADD CONSTRAINT ck_valid_currency CHECK (currency IN ('ARS', 'USD', 'EUR'));

-- Mejorar constraint de order_items para asientos vs cantidad
ALTER TABLE orders.order_items 
DROP CONSTRAINT IF EXISTS "order_items_check",
ADD CONSTRAINT ck_seat_or_quantity CHECK (
  (event_venue_seat_id IS NOT NULL AND quantity = 1) OR 
  (event_venue_seat_id IS NULL AND quantity > 0)
);

-- Agregar constraint de precios positivos
ALTER TABLE orders.order_items 
ADD CONSTRAINT ck_positive_price CHECK (unit_price_cents >= 0);

-- Índices para performance
CREATE INDEX IF NOT EXISTS idx_orders_status_created ON orders.orders(status, created_at);
CREATE INDEX IF NOT EXISTS idx_orders_customer_status ON orders.orders(customer_id, status);
CREATE INDEX IF NOT EXISTS idx_orders_organizer_status ON orders.orders(organizer_id, status);
CREATE INDEX IF NOT EXISTS idx_order_items_occurrence ON orders.order_items(occurrence_id);
CREATE INDEX IF NOT EXISTS idx_order_items_ticket_type ON orders.order_items(ticket_type_id);

-- Índice para búsquedas por referencia externa
CREATE INDEX IF NOT EXISTS idx_orders_external_ref ON orders.orders(external_reference) 
WHERE external_reference IS NOT NULL;

-- Función para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION orders.update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers para updated_at
CREATE TRIGGER update_orders_updated_at BEFORE UPDATE ON orders.orders 
FOR EACH ROW EXECUTE FUNCTION orders.update_updated_at_column();

CREATE TRIGGER update_customers_updated_at BEFORE UPDATE ON orders.customers 
FOR EACH ROW EXECUTE FUNCTION orders.update_updated_at_column();

CREATE TRIGGER update_order_items_updated_at BEFORE UPDATE ON orders.order_items 
FOR EACH ROW EXECUTE FUNCTION orders.update_updated_at_column();
