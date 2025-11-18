-- Cambiar el tipo de columna order_id de BIGINT a VARCHAR(255)
-- para soportar UUID en lugar de números secuenciales

-- Cambiar en payment_intents
ALTER TABLE payments.payment_intents 
ALTER COLUMN order_id TYPE VARCHAR(255);

-- Cambiar en refunds
ALTER TABLE payments.refunds 
ALTER COLUMN order_id TYPE VARCHAR(255);

-- Crear índices si aún no existen
CREATE INDEX IF NOT EXISTS idx_payment_intents_order_id 
ON payments.payment_intents(order_id);

CREATE INDEX IF NOT EXISTS idx_refunds_order_id 
ON payments.refunds(order_id);

