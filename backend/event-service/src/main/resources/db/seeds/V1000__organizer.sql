-- Organizers
INSERT INTO events.organizers (id, name, slug, contact_email, phone_number)
VALUES
  ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Ultimate Fighting Championship', 'ufc-345', 'contact@ufc.com', '17025551234'),
  ('b2c3d4e5-f6a7-8901-bcde-f23456789012', 'Lollapalooza Argentina', 'lolla-arg-2025', 'info@lollapalooza.com.ar', '541144445678');

-- Categories
INSERT INTO events.categories (id, name, description, active)
VALUES
  ('c3d4e5f6-a7b8-9012-cdef-345678901234', 'Conciertos', 'Eventos musicales en vivo de diferentes géneros y artistas.', true),
  ('d4e5f6a7-b8c9-0123-defa-456789012345', 'Deportes', 'Eventos deportivos profesionales y amateurs.', true);

-- Events (con venue y occurrence integrados)
INSERT INTO events.events (id, organizer_id, title, slug, description, category_id, cover_url, status, venue_name, venue_description, address_line, city, state, country, lat, lng, starts_at, ends_at, active)
VALUES
  ('e5f6a7b8-c9d0-1234-efab-567890123456', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Show de Fito Páez en el Monumento a la Bandera', 'fito-paez-monumento-rosario', 'Concierto especial de Fito Páez celebrando los 30 años de "El amor después del amor".', 'c3d4e5f6-a7b8-9012-cdef-345678901234', '/rock-festival-stage-lights.jpg', 'published', 'Estadio Monumental', 'Estadio Monumental de Núñez', 'Av. Monseñor José Félix Uriburu 1234', 'Buenos Aires', 'Buenos Aires', 'Argentina', -34.603684, -58.381559, '2025-11-15T20:00:00-03:00', '2025-11-15T23:00:00-03:00', true),
  ('f6a7b8c9-d0e1-2345-fabc-678901234567', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Partido Talleres vs Belgrano', 'talleres-vs-belgrano', 'Clásico cordobés en el Estadio Mario Alberto Kempes, fecha 12 del torneo local.', 'd4e5f6a7-b8c9-0123-defa-456789012345', '/football-soccer-stadium-match-crowd.jpg', 'published', 'Estadio Mario Alberto Kempes', 'Estadio de Córdoba', 'Avenida Ramón Cárcano', 'Córdoba', 'Córdoba', 'Argentina', -31.4427, -64.2356, '2025-10-25T18:00:00-03:00', '2025-10-25T20:00:00-03:00', true);

-- Seat Types
INSERT INTO events.seat_types (id, code, name, description)
VALUES
  ('a7b8c9d0-e1f2-3456-abcd-789012345678', 'VIP', 'VIP', 'Asientos en la zona VIP'),
  ('b8c9d0e1-f2a3-4567-bcde-890123456789', 'PLATEA', 'Platea', 'Asientos en la platea'),
  ('c9d0e1f2-a3b4-5678-cdef-901234567890', 'GENERAL', 'General', 'Asientos en la zona general'),
  ('d0e1f2a3-b4c5-6789-defa-012345678901', 'CAMPO', 'Campo', 'Asientos en la zona campo'),
  ('e1f2a3b4-c5d6-7890-efab-123456789012', 'PULLMAN', 'Pullman', 'Asientos tipo butaca premium, generalmente reclinables y con mayor comodidad, ubicados en zonas preferenciales.');

-- Areas (antes venue_areas, ahora vinculadas a eventos)
INSERT INTO events.areas (id, event_id, name, is_general_admission, capacity, position)
VALUES
  ('f2a3b4c5-d6e7-8901-fabc-234567890123', 'e5f6a7b8-c9d0-1234-efab-567890123456', 'Campo', true, 5000, 1),
  ('a3b4c5d6-e7f8-9012-abcd-345678901234', 'f6a7b8c9-d0e1-2345-fabc-678901234567', 'Zona VIP', false, 150, 2);

-- Area Pricing (precios por área)
INSERT INTO events.area_pricing (area_id, price_cents, currency)
VALUES
  ('f2a3b4c5-d6e7-8901-fabc-234567890123', 500000, 'ARS'),  -- Campo: $50000.00 ARS
  ('a3b4c5d6-e7f8-9012-abcd-345678901234', 15000000, 'ARS'); -- Zona VIP: $150000.00 ARS

-- Seats (antes venue_seats)
INSERT INTO events.seats (area_id, seat_number, row_number, label)
VALUES
  ('a3b4c5d6-e7f8-9012-abcd-345678901234', 1, 1, '1-1'),
  ('a3b4c5d6-e7f8-9012-abcd-345678901234', 2, 1, '1-2'),
  ('a3b4c5d6-e7f8-9012-abcd-345678901234', 3, 1, '1-3'),
  ('a3b4c5d6-e7f8-9012-abcd-345678901234', 4, 1, '1-4');
