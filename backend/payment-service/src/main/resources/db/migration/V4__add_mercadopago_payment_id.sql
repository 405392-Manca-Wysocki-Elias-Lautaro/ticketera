-- Agregar columna para almacenar el ID del pago en MercadoPago
ALTER TABLE payments.payment_intents 
ADD COLUMN IF NOT EXISTS mercadopago_payment_id BIGINT;

-- Agregar índice para búsquedas rápidas por payment_id de MercadoPago
CREATE INDEX IF NOT EXISTS idx_payment_intents_mercadopago_payment_id 
ON payments.payment_intents(mercadopago_payment_id);

-- Agregar comentarios
COMMENT ON COLUMN payments.payment_intents.mercadopago_payment_id IS 'ID del pago en MercadoPago (se obtiene del webhook)';

