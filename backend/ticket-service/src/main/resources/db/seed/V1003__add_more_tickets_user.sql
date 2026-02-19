-- =====================================================
-- 🎟️ SEED: 3 Tickets adicionales para user@ticketly.com
-- =====================================================
-- Event ID: 11111111-1111-1111-1111-111111111111 (Rock Nacional)
-- Area ID: a1111111-1111-1111-1111-111111111111 (Campo Delantero)
-- =====================================================

DO $$
DECLARE
    fetched_user_id UUID;
    v_event_id UUID := '11111111-1111-1111-1111-111111111111';
    v_area_id UUID := 'a1111111-1111-1111-1111-111111111111';
BEGIN
    -- 1. Buscar el ID del usuario
    SELECT id INTO fetched_user_id FROM auth.users WHERE email = 'user@ticketly.com';

    -- 2. Si el usuario existe, insertar tickets
    IF fetched_user_id IS NOT NULL THEN
        -- Ticket 4
        INSERT INTO tickets.tickets (
            order_item_id, event_id, event_venue_area_id, event_venue_seat_id, user_id,
            code, qr_token, price, currency, discount, final_price,
            status, issued_at, expires_at, created_at, updated_at
        ) VALUES (
            gen_random_uuid(), v_event_id, v_area_id, NULL, fetched_user_id,
            'TICKET-USER-004', 'QR-USER-004-TOKEN',
            35000.00, 'ARS', 0.00, 35000.00,
            'ISSUED', now(), '2026-02-20T23:00:00-03:00', now(), now()
        ) ON CONFLICT (code) DO NOTHING;

        -- Ticket 5
        INSERT INTO tickets.tickets (
            order_item_id, event_id, event_venue_area_id, event_venue_seat_id, user_id,
            code, qr_token, price, currency, discount, final_price,
            status, issued_at, expires_at, created_at, updated_at
        ) VALUES (
            gen_random_uuid(), v_event_id, v_area_id, NULL, fetched_user_id,
            'TICKET-USER-005', 'QR-USER-005-TOKEN',
            35000.00, 'ARS', 0.00, 35000.00,
            'ISSUED', now(), '2026-02-20T23:00:00-03:00', now(), now()
        ) ON CONFLICT (code) DO NOTHING;

        -- Ticket 6
        INSERT INTO tickets.tickets (
            order_item_id, event_id, event_venue_area_id, event_venue_seat_id, user_id,
            code, qr_token, price, currency, discount, final_price,
            status, issued_at, expires_at, created_at, updated_at
        ) VALUES (
            gen_random_uuid(), v_event_id, v_area_id, NULL, fetched_user_id,
            'TICKET-USER-006', 'QR-USER-006-TOKEN',
            35000.00, 'ARS', 0.00, 35000.00,
            'ISSUED', now(), '2026-02-20T23:00:00-03:00', now(), now()
        ) ON CONFLICT (code) DO NOTHING;

        RAISE NOTICE 'Additional tickets created for user %', fetched_user_id;
    ELSE
        RAISE NOTICE 'User user@ticketly.com not found. Skipping additional ticket creation.';
    END IF;
END $$;
