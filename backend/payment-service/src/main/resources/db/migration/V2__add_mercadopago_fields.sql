-- Agregar campos para la integración de Mercado Pago Checkout Pro
ALTER TABLE payments.payment_intents
ADD COLUMN IF NOT EXISTS preference_id VARCHAR(255),
ADD COLUMN IF NOT EXISTS payment_url VARCHAR(500),
ADD COLUMN IF NOT EXISTS error_message VARCHAR(1000),
ADD COLUMN IF NOT EXISTS customer_email VARCHAR(255),
ADD COLUMN IF NOT EXISTS customer_name VARCHAR(255),
ADD COLUMN IF NOT EXISTS description VARCHAR(500);

-- Crear índices para mejorar las consultas
CREATE INDEX IF NOT EXISTS idx_payment_intents_preference_id ON payments.payment_intents(preference_id);
CREATE INDEX IF NOT EXISTS idx_payment_intents_order_id ON payments.payment_intents(order_id);
CREATE INDEX IF NOT EXISTS idx_payment_intents_provider_ref ON payments.payment_intents(provider_ref);

