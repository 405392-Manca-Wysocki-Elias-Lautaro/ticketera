-- =========================================================
-- Migración V9: Corrección de montos de cupones FIXED_AMOUNT
-- =========================================================
-- Descripción: En la V8 se insertaron montos de 500000 
--              (pensando en centavos), pero la lógica aplica 
--              * 100 sobre este valor. 
--              Se corrigen a 5000 (pesos).

UPDATE orders.coupons
SET discount_value = 5000
WHERE code IN (
    'ROCK-OFF500',
    'CARROZA-OFF500',
    'TECH-OFF500',
    'ASADO-OFF500',
    'FITO-OFF500',
    'FUTBOL-OFF500'
) AND discount_type = 'FIXED_AMOUNT' AND discount_value = 500000;
