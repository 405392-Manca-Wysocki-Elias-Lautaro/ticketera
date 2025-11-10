-- Insertar el proveedor de pago de Mercado Pago
INSERT INTO payments.payment_providers (id, code, name, created_at, updated_at)
VALUES (1, 'mercadopago', 'Mercado Pago', now(), now())
ON CONFLICT (id) DO NOTHING;

-- Asegurar que la secuencia esté actualizada
SELECT setval('payments.payment_providers_id_seq', (SELECT MAX(id) FROM payments.payment_providers));

