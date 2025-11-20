-- Organizers
INSERT INTO events.organizers (id, name, slug, contact_email, phone_number)
VALUES
  ('11111111-1111-1111-1111-111111111111', 'Ultimate Fighting Championship', 'ufc-345', 'contact@ufc.com', '17025551234'),
  ('22222222-2222-2222-2222-222222222222', 'Lollapalooza Argentina', 'lolla-arg-2025', 'info@lollapalooza.com.ar', '541144445678');

-- Categories
INSERT INTO events.categories (id, name, description, active)
VALUES
  ('bbbbbbbb-1111-1111-1111-bbbbbbbbbbbb', 'Conciertos', 'Eventos musicales en vivo de diferentes géneros y artistas.', true),
  ('bbbbbbbb-2222-2222-2222-bbbbbbbbbbbb', 'Deportes', 'Eventos deportivos profesionales y amateurs.', true);

-- Events (con venue y occurrence integrados)
INSERT INTO events.events (id, organizer_id, title, slug, description, category_id, cover_url, status, venue_name, venue_description, address_line, city, state, country, lat, lng, starts_at, ends_at, active)
VALUES
  ('cccccccc-1111-1111-1111-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'Show de Fito Páez en el Monumento a la Bandera', 'fito-paez-monumento-rosario', 'Concierto especial de Fito Páez celebrando los 30 años de "El amor después del amor".', 'bbbbbbbb-1111-1111-1111-bbbbbbbbbbbb', '/rock-festival-stage-lights.jpg', 'published', 'Estadio Monumental', 'Estadio Monumental de Núñez', 'Av. Monseñor José Félix Uriburu 1234', 'Buenos Aires', 'Buenos Aires', 'Argentina', -34.603684, -58.381559, '2025-11-15 20:00:00-03', '2025-11-15 23:00:00-03', true),
  ('cccccccc-2222-2222-2222-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'Partido Newell''s vs Rosario Central', 'newells-vs-central', 'Clásico rosarino en el Estadio Marcelo Bielsa, fecha 12 del torneo local.', 'bbbbbbbb-2222-2222-2222-bbbbbbbbbbbb', '/football-soccer-stadium-match-crowd.jpg', 'published', 'Estadio Mario Alberto Kempes', 'Estadio de Córdoba', 'Avenida Ramón Cárcano', 'Córdoba', 'Córdoba', 'Argentina', -31.4427, -64.2356, '2025-10-25 18:00:00-03', '2025-10-25 20:00:00-03', true);

-- Seat Types
INSERT INTO events.seat_types (id, code, name, description)
VALUES
  ('eeeeeeee-1111-1111-1111-eeeeeeeeeeee', 'VIP', 'VIP', 'Asientos en la zona VIP'),
  ('eeeeeeee-2222-2222-2222-eeeeeeeeeeee', 'PLATEA', 'Platea', 'Asientos en la platea'),
  ('eeeeeeee-3333-3333-3333-eeeeeeeeeeee', 'GENERAL', 'General', 'Asientos en la zona general'),
  ('eeeeeeee-4444-4444-4444-eeeeeeeeeeee', 'CAMPO', 'Campo', 'Asientos en la zona campo'),
  ('eeeeeeee-5555-5555-5555-eeeeeeeeeeee', 'PULLMAN', 'Pullman', 'Asientos tipo butaca premium, generalmente reclinables y con mayor comodidad, ubicados en zonas preferenciales.');

-- Areas (antes venue_areas, ahora vinculadas a eventos)
INSERT INTO events.areas (id, event_id, name, is_general_admission, capacity, position)
VALUES
  ('11111111-1111-1111-1111-111111111111', 'cccccccc-1111-1111-1111-cccccccccccc', 'Campo', true, 5000, 1),
  ('22222222-2222-2222-2222-222222222222', 'cccccccc-2222-2222-2222-cccccccccccc', 'Zona VIP', false, 150, 2);

-- Area Pricing (precios por área)
INSERT INTO events.area_pricing (area_id, price_cents, currency)
VALUES
  ('11111111-1111-1111-1111-111111111111', 5000, 'ARS'),  -- Campo: $50.00 ARS
  ('22222222-2222-2222-2222-222222222222', 15000, 'ARS'); -- Zona VIP: $150.00 ARS

-- Seats (antes venue_seats)
INSERT INTO events.seats (area_id, seat_number, row_number, label)
VALUES
  ('22222222-2222-2222-2222-222222222222', 1, 1, '1-1'),
  ('22222222-2222-2222-2222-222222222222', 2, 1, '1-2'),
  ('22222222-2222-2222-2222-222222222222', 3, 1, '1-3'),
  ('22222222-2222-2222-2222-222222222222', 4, 1, '1-4');
