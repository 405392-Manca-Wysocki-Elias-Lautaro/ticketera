-- Añadir 4 eventos adicionales para el usuario admin y su organización

-- Insertar nuevas categorías si no existen (evitando duplicados por nombre)
INSERT INTO events.categories (id, name, description, active)
VALUES
  ('f7a8b9c0-d1e2-3456-fabc-789012345678', 'Tecnología', 'Conferencias, meetups y eventos relacionados con tecnología e innovación.', true),
  ('a8b9c0d1-e2f3-4567-abcd-890123456789', 'Gastronomía', 'Festivales gastronómicos, degustaciones y eventos culinarios.', true)
ON CONFLICT (name) DO NOTHING;

-- Asegurar que 'Teatro' existe (ya debería estar por V1003, pero por si acaso)
INSERT INTO events.categories (name, description, active)
VALUES ('Teatro', 'Obras de teatro, stand-up comedy y espectáculos escénicos.', true)
ON CONFLICT (name) DO NOTHING;

-- Insertar 4 nuevos eventos para el usuario admin (organizer_id: c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0)
-- Buscamos los IDs de categoría dinámicamente
INSERT INTO events.events (id, organizer_id, title, slug, description, category_id, cover_url, status, venue_name, venue_description, address_line, city, state, country, lat, lng, starts_at, ends_at, active)
VALUES
  -- Evento 1: Concierto de Rock Nacional
  ('11111111-1111-1111-1111-111111111111', 'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0', 'Rock Nacional: Los Auténticos Decadentes', 'rock-nacional-decadentes', 'Los Auténticos Decadentes presentan su nuevo álbum en un show imperdible con todos sus clásicos.', 
   (SELECT id FROM events.categories WHERE name = 'Teatro' LIMIT 1), -- Usamos Teatro temporalmente o buscamos otra si existiera Música
   '/rock-festival-concert-stage-lights-crowd.jpg', 'published', 'Movistar Arena', 'Estadio cubierto de última generación', 'Av. Corrientes 6271', 'Buenos Aires', 'Buenos Aires', 'Argentina', -34.6037, -58.4214, '2026-02-20T20:00:00-03:00', '2026-02-20T23:00:00-03:00', true),
  
  -- Evento 2: Obra de Teatro
  ('22222222-2222-2222-2222-222222222222', 'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0', 'Esperando la Carroza - El Musical', 'esperando-la-carroza-musical', 'La clásica comedia argentina llevada al formato musical con un elenco de lujo.', 
   (SELECT id FROM events.categories WHERE name = 'Teatro' LIMIT 1),
   '/theater-play-stage-performance-dramatic-lighting.jpg', 'published', 'Teatro El Nacional', 'Teatro histórico en el barrio de San Nicolás', 'Av. Corrientes 960', 'Buenos Aires', 'Buenos Aires', 'Argentina', -34.6037, -58.3816, '2026-02-20T19:30:00-03:00', '2026-02-20T22:00:00-03:00', true),
  
  -- Evento 3: Conferencia de Tecnología
  ('33333333-3333-3333-3333-333333333333', 'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0', 'TechConf Argentina 2025', 'techconf-argentina-2025', 'La conferencia de tecnología más importante de Argentina. Charlas sobre IA, Cloud, DevOps y más.', 
   (SELECT id FROM events.categories WHERE name = 'Tecnología' LIMIT 1),
   '/tech-conference-modern.jpg', 'published', 'Centro de Convenciones Buenos Aires', 'Moderno centro de convenciones en Puerto Madero', 'Av. Alicia Moreau de Justo 200', 'Buenos Aires', 'Buenos Aires', 'Argentina', -34.6118, -58.3632, '2026-02-20T15:00:00-03:00', '2026-02-20T22:00:00-03:00', true),
  
  -- Evento 4: Festival Gastronómico
  ('44444444-4444-4444-4444-444444444444', 'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0', 'Festival del Asado Argentino', 'festival-asado-argentino', 'El mejor festival de asado con parrilleros de todo el país. Degustaciones, música en vivo y más.', 
   (SELECT id FROM events.categories WHERE name = 'Gastronomía' LIMIT 1),
   '/jazz-concert-intimate-venue-saxophone-piano.jpg', 'published', 'Parque de la Ciudad', 'Amplio espacio verde al aire libre', 'Av. Fernández de la Cruz 4200', 'Buenos Aires', 'Buenos Aires', 'Argentina', -34.6753, -58.4789, '2026-02-20T17:00:00-03:00', '2026-02-20T23:00:00-03:00', true)
ON CONFLICT (id) DO NOTHING;

-- Crear áreas para cada evento

-- Áreas para Rock Nacional (Evento 1)
INSERT INTO events.areas (id, event_id, name, is_general_admission, capacity, position)
VALUES
  ('a1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'Campo Delantero', true, 3000, 1),
  ('a1111111-1111-1111-1111-111111111112', '11111111-1111-1111-1111-111111111111', 'Platea Alta', false, 500, 2)
ON CONFLICT (id) DO NOTHING;

-- Áreas para Teatro (Evento 2)
INSERT INTO events.areas (id, event_id, name, is_general_admission, capacity, position)
VALUES
  ('a2222222-2222-2222-2222-222222222221', '22222222-2222-2222-2222-222222222222', 'Platea', false, 200, 1),
  ('a2222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 'Pullman', false, 100, 2)
ON CONFLICT (id) DO NOTHING;

-- Áreas para TechConf (Evento 3)
INSERT INTO events.areas (id, event_id, name, is_general_admission, capacity, position)
VALUES
  ('a3333333-3333-3333-3333-333333333331', '33333333-3333-3333-3333-333333333333', 'Early Bird', true, 500, 1),
  ('a3333333-3333-3333-3333-333333333332', '33333333-3333-3333-3333-333333333333', 'General', true, 1000, 2)
ON CONFLICT (id) DO NOTHING;

-- Áreas para Festival Gastronómico (Evento 4)
INSERT INTO events.areas (id, event_id, name, is_general_admission, capacity, position)
VALUES
  ('a4444444-4444-4444-4444-444444444441', '44444444-4444-4444-4444-444444444444', 'Entrada General', true, 5000, 1),
  ('a4444444-4444-4444-4444-444444444442', '44444444-4444-4444-4444-444444444444', 'VIP Gourmet', false, 200, 2)
ON CONFLICT (id) DO NOTHING;

-- Precios para las áreas

-- Precios Rock Nacional
INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'a1111111-1111-1111-1111-111111111111', 3500000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'a1111111-1111-1111-1111-111111111111');

INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'a1111111-1111-1111-1111-111111111112', 5000000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'a1111111-1111-1111-1111-111111111112');

-- Precios Teatro
INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'a2222222-2222-2222-2222-222222222221', 2500000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'a2222222-2222-2222-2222-222222222221');

INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'a2222222-2222-2222-2222-222222222222', 4000000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'a2222222-2222-2222-2222-222222222222');

-- Precios TechConf
INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'a3333333-3333-3333-3333-333333333331', 1500000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'a3333333-3333-3333-3333-333333333331');

INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'a3333333-3333-3333-3333-333333333332', 2500000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'a3333333-3333-3333-3333-333333333332');

-- Precios Festival Gastronómico
INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'a4444444-4444-4444-4444-444444444441', 1000000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'a4444444-4444-4444-4444-444444444441');

INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'a4444444-4444-4444-4444-444444444442', 3000000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'a4444444-4444-4444-4444-444444444442');

-- Crear algunos asientos para las áreas numeradas (no general admission)

-- Asientos para Platea Alta del Rock Nacional
INSERT INTO events.seats (area_id, seat_number, row_number, label)
SELECT 
  'a1111111-1111-1111-1111-111111111112',
  s.n,
  1,
  '1-' || s.n::text
FROM generate_series(1, 10) AS s(n)
WHERE NOT EXISTS (
  SELECT 1 FROM events.seats 
  WHERE area_id = 'a1111111-1111-1111-1111-111111111112' AND row_number = 1 AND seat_number = s.n
);

-- Asientos para Platea del Teatro
INSERT INTO events.seats (area_id, seat_number, row_number, label)
SELECT 
  'a2222222-2222-2222-2222-222222222221',
  s.seat,
  r.row,
  r.row::text || '-' || s.seat::text
FROM generate_series(1, 10) AS r(row),
     generate_series(1, 20) AS s(seat)
WHERE NOT EXISTS (
  SELECT 1 FROM events.seats 
  WHERE area_id = 'a2222222-2222-2222-2222-222222222221' AND row_number = r.row AND seat_number = s.seat
);

-- Asientos para Pullman del Teatro
INSERT INTO events.seats (area_id, seat_number, row_number, label)
SELECT 
  'a2222222-2222-2222-2222-222222222222',
  s.seat,
  r.row,
  r.row::text || '-' || s.seat::text
FROM generate_series(1, 10) AS r(row),
     generate_series(1, 10) AS s(seat)
WHERE NOT EXISTS (
  SELECT 1 FROM events.seats 
  WHERE area_id = 'a2222222-2222-2222-2222-222222222222' AND row_number = r.row AND seat_number = s.seat
);

-- Asientos para VIP Gourmet del Festival
INSERT INTO events.seats (area_id, seat_number, row_number, label)
SELECT 
  'a4444444-4444-4444-4444-444444444442',
  s.seat,
  r.row,
  r.row::text || '-' || s.seat::text
FROM generate_series(1, 10) AS r(row),
     generate_series(1, 20) AS s(seat)
WHERE NOT EXISTS (
  SELECT 1 FROM events.seats 
  WHERE area_id = 'a4444444-4444-4444-4444-444444444442' AND row_number = r.row AND seat_number = s.seat
);
