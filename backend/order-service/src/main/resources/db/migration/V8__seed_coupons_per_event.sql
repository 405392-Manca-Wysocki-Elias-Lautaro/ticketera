-- =========================================================
-- Migración V8: Cupones de monto fijo por evento activo
-- =========================================================
-- Fecha: 2026-02-20
-- Descripción: Seed de cupones FIXED_AMOUNT, uno por cada
--              evento activo de todos los organizadores.
--
-- Organizer admin: c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0 (admin@ticketly.com)
--   11111111-1111-1111-1111-111111111111 → Rock Nacional: Los Auténticos Decadentes
--   22222222-2222-2222-2222-222222222222 → Esperando la Carroza - El Musical
--   33333333-3333-3333-3333-333333333333 → TechConf Argentina 2025
--   44444444-4444-4444-4444-444444444444 → Festival del Asado Argentino
--
-- Organizer original: a1b2c3d4-e5f6-7890-abcd-ef1234567890
--   e5f6a7b8-c9d0-1234-efab-567890123456 → Fito Páez en Vivo
--   f6a7b8c9-d0e1-2345-fabc-678901234567 → Newell's vs Central
--
-- Códigos generados:
--   ROCK-OFF500    → $5.000 — Rock Nacional
--   CARROZA-OFF500 → $5.000 — Teatro
--   TECH-OFF500    → $5.000 — TechConf
--   ASADO-OFF500   → $5.000 — Festival Asado
--   FITO-OFF500    → $5.000 — Fito Páez en Vivo
--   FUTBOL-OFF500  → $5.000 — Newell's vs Central
-- =========================================================

INSERT INTO orders.coupons (
    id,
    organizer_id,
    code,
    description,
    discount_type,
    discount_value,
    currency,
    max_uses,
    max_uses_per_customer,
    current_uses,
    valid_from,
    valid_until,
    event_ids,
    min_purchase_amount_cents,
    status,
    created_at,
    updated_at
)
VALUES
    -- Cupón para Rock Nacional: Los Auténticos Decadentes
    (
        'e1000001-0000-0000-0000-000000000001',
        'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0',
        'ROCK-OFF500',
        '$5.000 de descuento en entradas para Rock Nacional: Los Auténticos Decadentes',
        'FIXED_AMOUNT', 500000, 'ARS',
        100, 1, 0,
        NOW() - INTERVAL '1 day', NOW() + INTERVAL '30 days',
        ARRAY['11111111-1111-1111-1111-111111111111']::uuid[],
        NULL, 'ACTIVE',
        NOW(), NOW()
    ),

    -- Cupón para Esperando la Carroza - El Musical
    (
        'e1000002-0000-0000-0000-000000000002',
        'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0',
        'CARROZA-OFF500',
        '$5.000 de descuento en entradas para Esperando la Carroza - El Musical',
        'FIXED_AMOUNT', 500000, 'ARS',
        100, 1, 0,
        NOW() - INTERVAL '1 day', NOW() + INTERVAL '30 days',
        ARRAY['22222222-2222-2222-2222-222222222222']::uuid[],
        NULL, 'ACTIVE',
        NOW(), NOW()
    ),

    -- Cupón para TechConf Argentina 2025
    (
        'e1000003-0000-0000-0000-000000000003',
        'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0',
        'TECH-OFF500',
        '$5.000 de descuento en entradas para TechConf Argentina 2025',
        'FIXED_AMOUNT', 500000, 'ARS',
        100, 1, 0,
        NOW() - INTERVAL '1 day', NOW() + INTERVAL '30 days',
        ARRAY['33333333-3333-3333-3333-333333333333']::uuid[],
        NULL, 'ACTIVE',
        NOW(), NOW()
    ),

    -- Cupón para Festival del Asado Argentino
    (
        'e1000004-0000-0000-0000-000000000004',
        'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0',
        'ASADO-OFF500',
        '$5.000 de descuento en entradas para Festival del Asado Argentino',
        'FIXED_AMOUNT', 500000, 'ARS',
        100, 1, 0,
        NOW() - INTERVAL '1 day', NOW() + INTERVAL '30 days',
        ARRAY['44444444-4444-4444-4444-444444444444']::uuid[],
        NULL, 'ACTIVE',
        NOW(), NOW()
    )

ON CONFLICT (id) DO NOTHING;

-- =========================================================
-- Cupones para el organizador original (a1b2c3d4-e5f6-7890-abcd-ef1234567890)
-- Nota: FK lógica — el organizer_id no tiene tabla en orders, es referencia cruzada
-- =========================================================
INSERT INTO orders.coupons (
    id,
    organizer_id,
    code,
    description,
    discount_type,
    discount_value,
    currency,
    max_uses,
    max_uses_per_customer,
    current_uses,
    valid_from,
    valid_until,
    event_ids,
    min_purchase_amount_cents,
    status,
    created_at,
    updated_at
)
VALUES
    -- Cupón para Fito Páez en Vivo
    (
        'e1000005-0000-0000-0000-000000000005',
        'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
        'FITO-OFF500',
        '$5.000 de descuento en entradas para Fito Páez en Vivo',
        'FIXED_AMOUNT', 500000, 'ARS',
        100, 1, 0,
        NOW() - INTERVAL '1 day', NOW() + INTERVAL '30 days',
        ARRAY['e5f6a7b8-c9d0-1234-efab-567890123456']::uuid[],
        NULL, 'ACTIVE',
        NOW(), NOW()
    ),

    -- Cupón para Newell's vs Central
    (
        'e1000006-0000-0000-0000-000000000006',
        'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
        'FUTBOL-OFF500',
        '$5.000 de descuento en entradas para Newell''s vs Central',
        'FIXED_AMOUNT', 500000, 'ARS',
        100, 1, 0,
        NOW() - INTERVAL '1 day', NOW() + INTERVAL '30 days',
        ARRAY['f6a7b8c9-d0e1-2345-fabc-678901234567']::uuid[],
        NULL, 'ACTIVE',
        NOW(), NOW()
    )

ON CONFLICT (id) DO NOTHING;
