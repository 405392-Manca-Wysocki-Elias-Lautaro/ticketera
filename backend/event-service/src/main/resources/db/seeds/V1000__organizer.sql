-- Organizers
INSERT INTO events.organizers (id, name, slug, contact_email, phone_number)
VALUES
  ('11111111-1111-1111-1111-111111111111', 'Ultimate Fighting Championship', 'ufc-345', 'contact@ufc.com', '17025551234'),
  ('22222222-2222-2222-2222-222222222222', 'Lollapalooza Argentina', 'lolla-arg-2025', 'info@lollapalooza.com.ar', '541144445678');

-- Venues
INSERT INTO events.venues (id, organizer_id, name, description, address_line, city, state, country, lat, lng)
VALUES
  ('aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1', '11111111-1111-1111-1111-111111111111', 'Estadio Monumental', 'Estadio Monumental de Núñez', 'Av. Monseñor José Félix Uriburu 1234', 'Buenos Aires', 'Buenos Aires', 'Argentina', -34.603684, -58.381559),
  ('aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2', '22222222-2222-2222-2222-222222222222', 'Estadio Mario Alberto Kempes', 'Estadio de Córdoba', 'Avenida Ramón Cárcano', 'Córdoba', 'Córdoba', 'Argentina', -31.4427, -64.2356);

-- Categories
INSERT INTO events.categories (id, name, description, active)
VALUES
  ('bbbbbbbb-1111-1111-1111-bbbbbbbbbbbb', 'Conciertos', 'Eventos musicales en vivo de diferentes géneros y artistas.', true),
  ('bbbbbbbb-2222-2222-2222-bbbbbbbbbbbb', 'Deportes', 'Eventos deportivos profesionales y amateurs.', true);

-- Events
INSERT INTO events.events (id, organizer_id, title, slug, description, category_id, cover_url, status, active)
VALUES
  ('cccccccc-1111-1111-1111-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'Show de Fito Páez en el Monumento a la Bandera', 'fito-paez-monumento-rosario', 'Concierto especial de Fito Páez celebrando los 30 años de "El amor después del amor".', 'bbbbbbbb-1111-1111-1111-bbbbbbbbbbbb', 'https://example.com/images/fito-paez.jpg', 'published', true),
  ('cccccccc-2222-2222-2222-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'Partido Newell’s vs Rosario Central', 'newells-vs-central', 'Clásico rosarino en el Estadio Marcelo Bielsa, fecha 12 del torneo local.', 'bbbbbbbb-2222-2222-2222-bbbbbbbbbbbb', 'https://example.com/images/newells-central.jpg', 'published', true);

-- Occurrences
INSERT INTO events.occurrences (id, event_id, venue_id, starts_at, ends_at, status, slug, active)
VALUES
  ('dddddddd-1111-1111-1111-dddddddddddd', 'cccccccc-1111-1111-1111-cccccccccccc', 'aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1', '2025-11-15 20:00:00-03', '2025-11-15 23:00:00-03', 'draft', 'fito-paez-monumental-2025', true),
  ('dddddddd-2222-2222-2222-dddddddddddd', 'cccccccc-2222-2222-2222-cccccccccccc', 'aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2', '2025-10-25 18:00:00-03', '2025-10-25 20:00:00-03', 'draft', 'newells-vs-central-2025', true);

-- Seat Types
INSERT INTO events.seat_types (id, code, name, description)
VALUES
  ('eeeeeeee-1111-1111-1111-eeeeeeeeeeee', 'VIP', 'VIP', 'Asientos en la zona VIP'),
  ('eeeeeeee-2222-2222-2222-eeeeeeeeeeee', 'PLATEA', 'Platea', 'Asientos en la platea'),
  ('eeeeeeee-3333-3333-3333-eeeeeeeeeeee', 'GENERAL', 'General', 'Asientos en la zona general'),
  ('eeeeeeee-4444-4444-4444-eeeeeeeeeeee', 'CAMPO', 'Campo', 'Asientos en la zona campo'),
  ('eeeeeeee-5555-5555-5555-eeeeeeeeeeee', 'PULLMAN', 'Pullman', 'Asientos tipo butaca premium, generalmente reclinables y con mayor comodidad, ubicados en zonas preferenciales.');
  