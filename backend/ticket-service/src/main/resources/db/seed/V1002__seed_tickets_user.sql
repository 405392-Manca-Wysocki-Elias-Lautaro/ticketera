-- =====================================================
-- 🎟️ SEED: Tickets para user@ticketera.com
-- =====================================================
-- Event ID: e5f6a7b8-c9d0-1234-efab-567890123456 (Fito Páez - Monumento)
-- Area ID: f2a3b4c5-d6e7-8901-fabc-234567890123 (Campo)
-- User: Looked up from auth.users via email 'user@ticketera.com'
-- =====================================================

DO $$
DECLARE
    fetched_user_id UUID;
    v_event_id UUID := 'e5f6a7b8-c9d0-1234-efab-567890123456';
    v_area_id UUID := 'f2a3b4c5-d6e7-8901-fabc-234567890123';
BEGIN
    -- 1. Buscar el ID del usuario
    SELECT id INTO fetched_user_id FROM auth.users WHERE email = 'user@ticketera.com';

    -- 2. Si el usuario existe, insertar tickets
    IF fetched_user_id IS NOT NULL THEN
        -- Ticket 1
        INSERT INTO tickets.tickets (
            order_item_id, event_id, event_venue_area_id, event_venue_seat_id, user_id,
            code, qr_token, price, currency, discount, final_price,
            status, issued_at, expires_at, created_at, updated_at
        ) VALUES (
            gen_random_uuid(), v_event_id, v_area_id, NULL, fetched_user_id,
            'TICKET-USER-001', 'QR-USER-001-TOKEN',
            50000.00, 'ARS', 0.00, 50000.00,
            'ISSUED', now(), '2025-11-15T23:00:00-03:00', now(), now()
        ) ON CONFLICT (code) DO NOTHING;

        -- Ticket 2
        INSERT INTO tickets.tickets (
            order_item_id, event_id, event_venue_area_id, event_venue_seat_id, user_id,
            code, qr_token, price, currency, discount, final_price,
            status, issued_at, expires_at, created_at, updated_at
        ) VALUES (
            gen_random_uuid(), v_event_id, v_area_id, NULL, fetched_user_id,
            'TICKET-USER-002', 'QR-USER-002-TOKEN',
            50000.00, 'ARS', 0.00, 50000.00,
            'ISSUED', now(), '2025-11-15T23:00:00-03:00', now(), now()
        ) ON CONFLICT (code) DO NOTHING;

        -- Ticket 3
        INSERT INTO tickets.tickets (
            order_item_id, event_id, event_venue_area_id, event_venue_seat_id, user_id,
            code, qr_token, price, currency, discount, final_price,
            status, issued_at, expires_at, created_at, updated_at
        ) VALUES (
            gen_random_uuid(), v_event_id, v_area_id, NULL, fetched_user_id,
            'TICKET-USER-003', 'QR-USER-003-TOKEN',
            50000.00, 'ARS', 0.00, 50000.00,
            'ISSUED', now(), '2025-11-15T23:00:00-03:00', now(), now()
        ) ON CONFLICT (code) DO NOTHING;

        RAISE NOTICE 'Tickets created for user %', fetched_user_id;
    ELSE
        RAISE NOTICE 'User user@ticketera.com not found. Skipping ticket creation.';
    END IF;
END $$;
