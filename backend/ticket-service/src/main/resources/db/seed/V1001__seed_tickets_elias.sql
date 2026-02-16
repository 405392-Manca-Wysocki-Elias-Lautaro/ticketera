-- =====================================================
-- 🎟️ SEED: Tickets de ejemplo para usuario Elias (Super Admin)
-- =====================================================
-- User ID: a1b2c3d4-e5f6-7890-abcd-ef1234567890 (Elias)
-- Event ID: e5f6a7b8-c9d0-1234-efab-567890123456 (Fito Páez - Monumento)
-- Area ID: f2a3b4c5-d6e7-8901-fabc-234567890123 (Campo)
-- =====================================================

INSERT INTO tickets.tickets (
    order_item_id,
    event_id,
    event_venue_area_id,
    event_venue_seat_id,
    user_id,
    code,
    qr_token,
    price,
    currency,
    discount,
    final_price,
    status,
    issued_at,
    expires_at,
    created_at,
    updated_at
)
VALUES
  -- Ticket 1
  (
    gen_random_uuid(), -- Fake order_item_id
    'e5f6a7b8-c9d0-1234-efab-567890123456', -- Event: Fito Páez
    'f2a3b4c5-d6e7-8901-fabc-234567890123', -- Area: Campo
    NULL, -- Seat: N/A (General Admission)
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890', -- User: Elias
    'TICKET-ELIAS-001',
    'QR-ELIAS-001-TOKEN',
    50000.00, 'ARS', 0.00, 50000.00,
    'ISSUED',
    now(),
    '2025-11-15T23:00:00-03:00', -- Event ends at
    now(), now()
  ),
  -- Ticket 2
  (
    gen_random_uuid(),
    'e5f6a7b8-c9d0-1234-efab-567890123456',
    'f2a3b4c5-d6e7-8901-fabc-234567890123',
    NULL,
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'TICKET-ELIAS-002',
    'QR-ELIAS-002-TOKEN',
    50000.00, 'ARS', 0.00, 50000.00,
    'ISSUED',
    now(),
    '2025-11-15T23:00:00-03:00',
    now(), now()
  ),
  -- Ticket 3
  (
    gen_random_uuid(),
    'e5f6a7b8-c9d0-1234-efab-567890123456',
    'f2a3b4c5-d6e7-8901-fabc-234567890123',
    NULL,
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'TICKET-ELIAS-003',
    'QR-ELIAS-003-TOKEN',
    50000.00, 'ARS', 0.00, 50000.00,
    'ISSUED',
    now(),
    '2025-11-15T23:00:00-03:00',
    now(), now()
  ),
  -- Ticket 4
  (
    gen_random_uuid(),
    'e5f6a7b8-c9d0-1234-efab-567890123456',
    'f2a3b4c5-d6e7-8901-fabc-234567890123',
    NULL,
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'TICKET-ELIAS-004',
    'QR-ELIAS-004-TOKEN',
    50000.00, 'ARS', 0.00, 50000.00,
    'ISSUED',
    now(),
    '2025-11-15T23:00:00-03:00',
    now(), now()
  ),
  -- Ticket 5
  (
    gen_random_uuid(),
    'e5f6a7b8-c9d0-1234-efab-567890123456',
    'f2a3b4c5-d6e7-8901-fabc-234567890123',
    NULL,
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'TICKET-ELIAS-005',
    'QR-ELIAS-005-TOKEN',
    50000.00, 'ARS', 0.00, 50000.00,
    'ISSUED',
    now(),
    '2025-11-15T23:00:00-03:00',
    now(), now()
  )
ON CONFLICT (code) DO NOTHING;
