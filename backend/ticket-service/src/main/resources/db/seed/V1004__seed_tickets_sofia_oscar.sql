-- =====================================================
-- 🎟️ SEED: Tickets para Sofia Enamorado y Oscar Botta
-- =====================================================

DO $$
DECLARE
    -- IDs de Usuarios
    id_sofia UUID := 'e0e0e0e0-e0e0-e0e0-e0e0-e0e0e0e0e0e1';
    id_oscar UUID := 'e0e0e0e0-e0e0-e0e0-e0e0-e0e0e0e0e0e2';

    -- Evento 1: Rock Nacional (11111111...)
    -- Área: Campo Delantero (a1111111...12) -> ID real en V1004_add_admin_events es ...11 para Campo y ...12 para Platea. Campo Delantero en V1004 es ...11.
    -- Vamos a usar Campo Delantero: a1111111-1111-1111-1111-111111111111
    event_rock UUID := '11111111-1111-1111-1111-111111111111';
    area_rock_campo UUID := 'a1111111-1111-1111-1111-111111111111';

    -- Evento 2: Teatro (22222222...)
    -- Área: Platea (a2222222...21)
    event_teatro UUID := '22222222-2222-2222-2222-222222222222';
    area_teatro_platea UUID := 'a2222222-2222-2222-2222-222222222221';

BEGIN
    -- Tickets para Sofia (Rock Nacional)
    INSERT INTO tickets.tickets (
        order_item_id, event_id, event_venue_area_id, event_venue_seat_id, user_id,
        code, qr_token, price, currency, discount, final_price,
        status, issued_at, expires_at, created_at, updated_at
    ) VALUES 
    (
        gen_random_uuid(), event_rock, area_rock_campo, NULL, id_sofia,
        'TICKET-SOFIA-001', 'QR-SOFIA-001-TOKEN',
        35000.00, 'ARS', 0.00, 35000.00,
        'ISSUED', now(), '2026-02-20T23:00:00-03:00', now(), now()
    ),
    (
        gen_random_uuid(), event_rock, area_rock_campo, NULL, id_sofia,
        'TICKET-SOFIA-002', 'QR-SOFIA-002-TOKEN',
        35000.00, 'ARS', 0.00, 35000.00,
        'ISSUED', now(), '2026-02-20T23:00:00-03:00', now(), now()
    )
    ON CONFLICT (code) DO NOTHING;

    -- Tickets para Oscar (Teatro)
    INSERT INTO tickets.tickets (
        order_item_id, event_id, event_venue_area_id, event_venue_seat_id, user_id,
        code, qr_token, price, currency, discount, final_price,
        status, issued_at, expires_at, created_at, updated_at
    ) VALUES 
    (
        gen_random_uuid(), event_teatro, area_teatro_platea, NULL, id_oscar,
        'TICKET-OSCAR-001', 'QR-OSCAR-001-TOKEN',
        25000.00, 'ARS', 0.00, 25000.00,
        'ISSUED', now(), '2026-02-20T23:00:00-03:00', now(), now()
    ),
    (
        gen_random_uuid(), event_teatro, area_teatro_platea, NULL, id_oscar,
        'TICKET-OSCAR-002', 'QR-OSCAR-002-TOKEN',
        25000.00, 'ARS', 0.00, 25000.00,
        'ISSUED', now(), '2026-02-20T23:00:00-03:00', now(), now()
    )
    ON CONFLICT (code) DO NOTHING;

    RAISE NOTICE 'Tickets created for Sofia (%) and Oscar (%)', id_sofia, id_oscar;
END $$;
