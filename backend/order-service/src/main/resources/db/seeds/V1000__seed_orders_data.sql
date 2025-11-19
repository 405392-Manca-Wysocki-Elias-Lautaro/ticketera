-- Seed data para métricas del organizador
-- Este archivo crea datos de prueba para que las queries de métricas retornen información

-- Customers
INSERT INTO orders.customers (id, email, first_name, last_name, phone, created_at, updated_at)
VALUES
  ('aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', 'cliente1@example.com', 'Juan', 'Pérez', '+541111111111', now() - INTERVAL '30 days', now() - INTERVAL '30 days'),
  ('aaaaaaaa-2222-2222-2222-aaaaaaaaaaaa', 'cliente2@example.com', 'María', 'González', '+541111111112', now() - INTERVAL '25 days', now() - INTERVAL '25 days'),
  ('aaaaaaaa-3333-3333-3333-aaaaaaaaaaaa', 'cliente3@example.com', 'Carlos', 'López', '+541111111113', now() - INTERVAL '20 days', now() - INTERVAL '20 days'),
  ('aaaaaaaa-4444-4444-4444-aaaaaaaaaaaa', 'cliente4@example.com', 'Ana', 'Martínez', '+541111111114', now() - INTERVAL '15 days', now() - INTERVAL '15 days'),
  ('aaaaaaaa-5555-5555-5555-aaaaaaaaaaaa', 'cliente5@example.com', 'Luis', 'Fernández', '+541111111115', now() - INTERVAL '10 days', now() - INTERVAL '10 days'),
  ('aaaaaaaa-6666-6666-6666-aaaaaaaaaaaa', 'cliente6@example.com', 'Laura', 'Rodríguez', '+541111111116', now() - INTERVAL '5 days', now() - INTERVAL '5 days'),
  ('aaaaaaaa-7777-7777-7777-aaaaaaaaaaaa', 'cliente7@example.com', 'Pedro', 'Sánchez', '+541111111117', now() - INTERVAL '3 days', now() - INTERVAL '3 days'),
  ('aaaaaaaa-8888-8888-8888-aaaaaaaaaaaa', 'cliente8@example.com', 'Sofía', 'Torres', '+541111111118', now() - INTERVAL '1 day', now() - INTERVAL '1 day')
ON CONFLICT (email) DO NOTHING;

-- Ticket Types (usaremos IDs consistentes)
-- Nota: Estos deberían existir en el schema events, pero si no existen, los creamos aquí como referencia
-- En realidad, solo necesitamos los UUIDs para el seed

-- Órdenes pagadas (organizer_id = '11111111-1111-1111-1111-111111111111' - UFC)
-- Algunas recientes (últimos 7 días) y algunas antiguas (más de 7 días)

-- Órdenes antiguas (hace más de 7 días) - para totales generales
INSERT INTO orders.orders (id, customer_id, organizer_id, status, total_cents, currency, created_at, updated_at, paid_at)
VALUES
  ('dddddddd-1111-1111-1111-dddddddddddd', 'aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'paid', 50000, 'ARS', now() - INTERVAL '30 days', now() - INTERVAL '30 days', now() - INTERVAL '30 days'),
  ('dddddddd-2222-2222-2222-dddddddddddd', 'aaaaaaaa-2222-2222-2222-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'paid', 150000, 'ARS', now() - INTERVAL '25 days', now() - INTERVAL '25 days', now() - INTERVAL '25 days'),
  ('dddddddd-3333-3333-3333-dddddddddddd', 'aaaaaaaa-3333-3333-3333-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'paid', 100000, 'ARS', now() - INTERVAL '20 days', now() - INTERVAL '20 days', now() - INTERVAL '20 days'),
  ('dddddddd-4444-4444-4444-dddddddddddd', 'aaaaaaaa-4444-4444-4444-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'paid', 75000, 'ARS', now() - INTERVAL '15 days', now() - INTERVAL '15 days', now() - INTERVAL '15 days'),
  ('dddddddd-5555-5555-5555-dddddddddddd', 'aaaaaaaa-5555-5555-5555-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'paid', 200000, 'ARS', now() - INTERVAL '10 days', now() - INTERVAL '10 days', now() - INTERVAL '10 days')
ON CONFLICT (id) DO NOTHING;

-- Órdenes recientes (últimos 7 días) - para métricas de última semana
INSERT INTO orders.orders (id, customer_id, organizer_id, status, total_cents, currency, created_at, updated_at, paid_at)
VALUES
  ('dddddddd-6666-6666-6666-dddddddddddd', 'aaaaaaaa-6666-6666-6666-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'paid', 125000, 'ARS', now() - INTERVAL '6 days', now() - INTERVAL '6 days', now() - INTERVAL '6 days'),
  ('dddddddd-7777-7777-7777-dddddddddddd', 'aaaaaaaa-7777-7777-7777-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'paid', 100000, 'ARS', now() - INTERVAL '4 days', now() - INTERVAL '4 days', now() - INTERVAL '4 days'),
  ('dddddddd-8888-8888-8888-dddddddddddd', 'aaaaaaaa-8888-8888-8888-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'paid', 175000, 'ARS', now() - INTERVAL '2 days', now() - INTERVAL '2 days', now() - INTERVAL '2 days'),
  ('dddddddd-9999-9999-9999-dddddddddddd', 'aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'paid', 90000, 'ARS', now() - INTERVAL '1 day', now() - INTERVAL '1 day', now() - INTERVAL '1 day')
ON CONFLICT (id) DO NOTHING;

-- Órdenes con otros estados (para asegurar que las queries solo cuenten 'paid')
INSERT INTO orders.orders (id, customer_id, organizer_id, status, total_cents, currency, created_at, updated_at)
VALUES
  ('dddddddd-aaaa-aaaa-aaaa-dddddddddddd', 'aaaaaaaa-2222-2222-2222-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'pending', 80000, 'ARS', now() - INTERVAL '5 days', now() - INTERVAL '5 days'),
  ('dddddddd-bbbb-bbbb-bbbb-dddddddddddd', 'aaaaaaaa-3333-3333-3333-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'failed', 60000, 'ARS', now() - INTERVAL '3 days', now() - INTERVAL '3 days')
ON CONFLICT (id) DO NOTHING;

-- Order Items para órdenes antiguas (más de 7 días)
-- Event ID: 'cccccccc-1111-1111-1111-cccccccccccc' (Fito Páez)
-- Event ID: 'cccccccc-2222-2222-2222-cccccccccccc' (Newell's vs Central)
-- Usamos event_id (después de la migración V4)
-- Ticket Type ID: usamos un UUID válido (en producción debería existir en events.ticket_types)
-- NOTA: Usamos prefijo 'f' para order_items para evitar conflictos con otros UUIDs
INSERT INTO orders.order_items (id, order_id, event_id, venue_area_id, venue_seat_id, ticket_type_id, unit_price_cents, quantity, created_at, updated_at)
VALUES
  -- Orden antigua 1: 10 tickets a $50 c/u = $500 total
  ('f1111111-1111-1111-1111-111111111111', 'dddddddd-1111-1111-1111-dddddddddddd', 'cccccccc-1111-1111-1111-cccccccccccc', '11111111-1111-1111-1111-111111111111', NULL, 'eeeeeeee-3333-3333-3333-eeeeeeeeeeee', 5000, 10, now() - INTERVAL '30 days', now() - INTERVAL '30 days'),
  
  -- Orden antigua 2: 3 tickets VIP a $150 c/u = $450 total
  ('f2222222-2222-2222-2222-222222222222', 'dddddddd-2222-2222-2222-dddddddddddd', 'cccccccc-2222-2222-2222-cccccccccccc', '22222222-2222-2222-2222-222222222222', NULL, 'eeeeeeee-2222-2222-2222-eeeeeeeeeeee', 15000, 3, now() - INTERVAL '25 days', now() - INTERVAL '25 days'),
  
  -- Orden antigua 3: 5 tickets a $100 c/u = $500 total (2 del evento 1 + 3 del evento 2)
  ('f3333333-3333-3333-3333-333333333333', 'dddddddd-3333-3333-3333-dddddddddddd', 'cccccccc-1111-1111-1111-cccccccccccc', '11111111-1111-1111-1111-111111111111', NULL, 'eeeeeeee-3333-3333-3333-eeeeeeeeeeee', 10000, 2, now() - INTERVAL '20 days', now() - INTERVAL '20 days'),
  ('f4444444-4444-4444-4444-444444444444', 'dddddddd-3333-3333-3333-dddddddddddd', 'cccccccc-2222-2222-2222-cccccccccccc', '22222222-2222-2222-2222-222222222222', NULL, 'eeeeeeee-1111-1111-1111-eeeeeeeeeeee', 10000, 3, now() - INTERVAL '20 days', now() - INTERVAL '20 days'),
  
  -- Orden antigua 4: 15 tickets a $50 c/u = $750 total
  ('f5555555-5555-5555-5555-555555555555', 'dddddddd-4444-4444-4444-dddddddddddd', 'cccccccc-1111-1111-1111-cccccccccccc', '11111111-1111-1111-1111-111111111111', NULL, 'eeeeeeee-3333-3333-3333-eeeeeeeeeeee', 5000, 15, now() - INTERVAL '15 days', now() - INTERVAL '15 days'),
  
  -- Orden antigua 5: 8 tickets VIP a $250 c/u = $2000 total
  ('f6666666-6666-6666-6666-666666666666', 'dddddddd-5555-5555-5555-dddddddddddd', 'cccccccc-2222-2222-2222-cccccccccccc', '22222222-2222-2222-2222-222222222222', NULL, 'eeeeeeee-1111-1111-1111-eeeeeeeeeeee', 25000, 8, now() - INTERVAL '10 days', now() - INTERVAL '10 days')
ON CONFLICT (id) DO NOTHING;

-- Order Items para órdenes recientes (últimos 7 días) - estas aparecerán en ticketsSoldLastWeek
INSERT INTO orders.order_items (id, order_id, event_id, venue_area_id, venue_seat_id, ticket_type_id, unit_price_cents, quantity, created_at, updated_at)
VALUES
  -- Orden reciente 1 (hace 6 días): 5 tickets a $125 c/u = $625 total
  ('f7777777-7777-7777-7777-777777777777', 'dddddddd-6666-6666-6666-dddddddddddd', 'cccccccc-1111-1111-1111-cccccccccccc', '11111111-1111-1111-1111-111111111111', NULL, 'eeeeeeee-3333-3333-3333-eeeeeeeeeeee', 12500, 5, now() - INTERVAL '6 days', now() - INTERVAL '6 days'),
  
  -- Orden reciente 2 (hace 4 días): 4 tickets a $100 c/u = $400 total (2 de cada evento)
  ('f8888888-8888-8888-8888-888888888888', 'dddddddd-7777-7777-7777-dddddddddddd', 'cccccccc-1111-1111-1111-cccccccccccc', '11111111-1111-1111-1111-111111111111', NULL, 'eeeeeeee-3333-3333-3333-eeeeeeeeeeee', 10000, 2, now() - INTERVAL '4 days', now() - INTERVAL '4 days'),
  ('f9999999-9999-9999-9999-999999999999', 'dddddddd-7777-7777-7777-dddddddddddd', 'cccccccc-2222-2222-2222-cccccccccccc', '22222222-2222-2222-2222-222222222222', NULL, 'eeeeeeee-2222-2222-2222-eeeeeeeeeeee', 10000, 2, now() - INTERVAL '4 days', now() - INTERVAL '4 days'),
  
  -- Orden reciente 3 (hace 2 días): 7 tickets VIP a $175 c/u = $1225 total
  ('faaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'dddddddd-8888-8888-8888-dddddddddddd', 'cccccccc-2222-2222-2222-cccccccccccc', '22222222-2222-2222-2222-222222222222', NULL, 'eeeeeeee-1111-1111-1111-eeeeeeeeeeee', 25000, 7, now() - INTERVAL '2 days', now() - INTERVAL '2 days'),
  
  -- Orden reciente 4 (hace 1 día): 9 tickets a $90 c/u = $810 total
  ('fbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'dddddddd-9999-9999-9999-dddddddddddd', 'cccccccc-1111-1111-1111-cccccccccccc', '11111111-1111-1111-1111-111111111111', NULL, 'eeeeeeee-3333-3333-3333-eeeeeeeeeeee', 10000, 9, now() - INTERVAL '1 day', now() - INTERVAL '1 day')
ON CONFLICT (id) DO NOTHING;

-- Order Items para órdenes con otros estados (NO deben contarse en las métricas)
INSERT INTO orders.order_items (id, order_id, event_id, venue_area_id, venue_seat_id, ticket_type_id, unit_price_cents, quantity, created_at, updated_at)
VALUES
  ('fccccccc-cccc-cccc-cccc-cccccccccccc', 'dddddddd-aaaa-aaaa-aaaa-dddddddddddd', 'cccccccc-1111-1111-1111-cccccccccccc', '11111111-1111-1111-1111-111111111111', NULL, 'eeeeeeee-3333-3333-3333-eeeeeeeeeeee', 8000, 10, now() - INTERVAL '5 days', now() - INTERVAL '5 days'),
  ('fddddddd-dddd-dddd-dddd-dddddddddddd', 'dddddddd-bbbb-bbbb-bbbb-dddddddddddd', 'cccccccc-2222-2222-2222-cccccccccccc', '22222222-2222-2222-2222-222222222222', NULL, 'eeeeeeee-1111-1111-1111-eeeeeeeeeeee', 6000, 10, now() - INTERVAL '3 days', now() - INTERVAL '3 days')
ON CONFLICT (id) DO NOTHING;

