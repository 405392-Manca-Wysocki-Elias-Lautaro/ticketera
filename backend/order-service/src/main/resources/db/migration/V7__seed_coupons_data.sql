-- =========================================================
-- Migración V7: Datos iniciales de cupones de prueba
-- =========================================================

-- Insertar cupones de ejemplo para el organizador del seed
-- organizer_id = 'a1b2c3d4-e5f6-7890-abcd-ef1234567890' (mismo del seed de órdenes)

INSERT INTO orders.coupons (
    id, organizer_id, code, description, discount_type, discount_value, currency,
    max_uses, max_uses_per_customer, current_uses, valid_from, valid_until,
    event_ids, min_purchase_amount_cents, status, created_at, updated_at
) VALUES
    -- Cupón ACTIVO: 10% de descuento, vigente
    (
        'c1111111-1111-1111-1111-111111111111',
        'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
        'DESCUENTO10',
        'Descuento del 10% en todas las entradas',
        'PERCENTAGE', 10, 'ARS',
        100, NULL, 3,
        NOW() - INTERVAL '30 days', NOW() + INTERVAL '60 days',
        NULL, NULL, 'ACTIVE',
        NOW() - INTERVAL '30 days', NOW()
    ),
    -- Cupón ACTIVO: $500 de descuento fijo
    (
        'c2222222-2222-2222-2222-222222222222',
        'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
        'DESCUENTO500',
        'Descuento fijo de $500 en tu compra',
        'FIXED_AMOUNT', 500, 'ARS',
        50, NULL, 8,
        NOW() - INTERVAL '15 days', NOW() + INTERVAL '45 days',
        NULL, NULL, 'ACTIVE',
        NOW() - INTERVAL '15 days', NOW()
    ),
    -- Cupón ACTIVO: 25% de descuento, uso limitado
    (
        'c3333333-3333-3333-3333-333333333333',
        'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
        'PROMO25',
        'Promoción especial 25% OFF - Tiempo limitado',
        'PERCENTAGE', 25, 'ARS',
        20, 1, 5,
        NOW() - INTERVAL '7 days', NOW() + INTERVAL '14 days',
        NULL, NULL, 'ACTIVE',
        NOW() - INTERVAL '7 days', NOW()
    ),
    -- Cupón ACTIVO: $2000 de descuento fijo, sin límite de usos
    (
        'c4444444-4444-4444-4444-444444444444',
        'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
        'MEGA2000',
        'Mega descuento de $2.000 en entradas VIP',
        'FIXED_AMOUNT', 2000, 'ARS',
        NULL, NULL, 12,
        NOW() - INTERVAL '20 days', NOW() + INTERVAL '40 days',
        NULL, NULL, 'ACTIVE',
        NOW() - INTERVAL '20 days', NOW()
    ),
    -- Cupón EXPIRADO: ya venció
    (
        'c5555555-5555-5555-5555-555555555555',
        'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
        'VERANO2025',
        'Promoción de verano 2025 - 15% OFF',
        'PERCENTAGE', 15, 'ARS',
        200, NULL, 47,
        NOW() - INTERVAL '90 days', NOW() - INTERVAL '10 days',
        NULL, NULL, 'EXPIRED',
        NOW() - INTERVAL '90 days', NOW() - INTERVAL '10 days'
    ),
    -- Cupón AGOTADO: alcanzó su máximo de usos
    (
        'c6666666-6666-6666-6666-666666666666',
        'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
        'FLASH50',
        'Descuento flash 50% - Limitado a 10 usos',
        'PERCENTAGE', 50, 'ARS',
        10, 1, 10,
        NOW() - INTERVAL '5 days', NOW() + INTERVAL '25 days',
        NULL, NULL, 'EXHAUSTED',
        NOW() - INTERVAL '5 days', NOW()
    ),
    -- Cupón INACTIVO: desactivado por el organizador
    (
        'c7777777-7777-7777-7777-777777777777',
        'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
        'BIENVENIDA',
        'Cupón de bienvenida $1.000 OFF - Desactivado temporalmente',
        'FIXED_AMOUNT', 1000, 'ARS',
        NULL, NULL, 22,
        NOW() - INTERVAL '60 days', NOW() + INTERVAL '30 days',
        NULL, NULL, 'INACTIVE',
        NOW() - INTERVAL '60 days', NOW()
    ),
    -- Cupón ACTIVO: 5% de descuento, recién creado
    (
        'c8888888-8888-8888-8888-888888888888',
        'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
        'NUEVO5',
        'Descuento del 5% para nuevos usuarios',
        'PERCENTAGE', 5, 'ARS',
        500, 1, 0,
        NOW(), NOW() + INTERVAL '90 days',
        NULL, NULL, 'ACTIVE',
        NOW(), NOW()
    )
ON CONFLICT (id) DO NOTHING;
