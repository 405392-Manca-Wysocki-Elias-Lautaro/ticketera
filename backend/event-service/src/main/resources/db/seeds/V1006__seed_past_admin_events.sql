-- ============================================================================
-- Seed: Eventos pasados del admin (admin@ticketly.com) para poblar métricas
-- Organizer ID: c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0
-- Fechas aleatorias en los últimos 6 meses (agosto 2025 - febrero 2026)
-- ============================================================================

-- ────────────────────────────────────────────────────
-- 10 eventos pasados con status 'published' y fechas ya transcurridas
-- ────────────────────────────────────────────────────
INSERT INTO events.events (id, organizer_id, title, slug, description, category_id, cover_url, status, venue_name, venue_description, address_line, city, state, country, lat, lng, starts_at, ends_at, active)
VALUES
  -- 1: Noche de Jazz en Palermo (15 ago 2025)
  ('e0e0e0e0-0001-4000-a001-000000000001', 'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0',
   'Noche de Jazz en Palermo', 'noche-jazz-palermo',
   'Un viaje musical por los clásicos del jazz con los mejores músicos del circuito porteño.',
   (SELECT id FROM events.categories WHERE name = 'Conciertos' LIMIT 1),
   '/jazz-concert-intimate-venue-saxophone-piano.jpg', 'published',
   'La Trastienda Club', 'Icónico club de música en vivo', 'Balcarce 460', 'Buenos Aires', 'Buenos Aires', 'Argentina',
   -34.6157, -58.3733, '2025-08-15T21:00:00-03:00', '2025-08-16T01:00:00-03:00', true),

  -- 2: Stand-Up Comedy Night (02 sep 2025)
  ('e0e0e0e0-0001-4000-a001-000000000002', 'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0',
   'Stand-Up Comedy Night', 'standup-comedy-night',
   'Los comediantes más filosos del país en una noche de humor sin filtro.',
   (SELECT id FROM events.categories WHERE name = 'Comedia' LIMIT 1),
   '/comedy-show-stage-microphone.jpg', 'published',
   'Teatro Metropolitan', 'Teatro emblemático de Av. Corrientes', 'Av. Corrientes 1343', 'Buenos Aires', 'Buenos Aires', 'Argentina',
   -34.6042, -58.3865, '2025-09-02T20:30:00-03:00', '2025-09-02T23:00:00-03:00', true),

  -- 3: Festival de Cine Independiente (18 sep 2025)
  ('e0e0e0e0-0001-4000-a001-000000000003', 'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0',
   'Festival de Cine Independiente', 'festival-cine-independiente',
   'Ciclo de proyecciones de películas independientes argentinas con debate posterior.',
   (SELECT id FROM events.categories WHERE name = 'Cine' LIMIT 1),
   '/cinema-festival-screen.jpg', 'published',
   'Centro Cultural San Martín', 'Espacio multicultural porteño', 'Sarmiento 1551', 'Buenos Aires', 'Buenos Aires', 'Argentina',
   -34.6046, -58.3911, '2025-09-18T17:00:00-03:00', '2025-09-18T22:00:00-03:00', true),

  -- 4: Taller de Fotografía Urbana (05 oct 2025)
  ('e0e0e0e0-0001-4000-a001-000000000004', 'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0',
   'Taller de Fotografía Urbana', 'taller-fotografia-urbana',
   'Aprende a capturar la esencia de la ciudad con técnicas profesionales de fotografía callejera.',
   (SELECT id FROM events.categories WHERE name = 'Taller' LIMIT 1),
   '/photography-workshop-urban.jpg', 'published',
   'Espacio Cultural Nuestros Hijos', 'Centro cultural en Av. de Mayo', 'Av. de Mayo 1396', 'Buenos Aires', 'Buenos Aires', 'Argentina',
   -34.6092, -58.3844, '2025-10-05T10:00:00-03:00', '2025-10-05T14:00:00-03:00', true),

  -- 5: Conferencia DevOps Argentina (22 oct 2025)
  ('e0e0e0e0-0001-4000-a001-000000000005', 'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0',
   'Conferencia DevOps Argentina', 'conferencia-devops-argentina',
   'Dos días de charlas y workshops sobre CI/CD, Kubernetes, observabilidad y cultura DevOps.',
   (SELECT id FROM events.categories WHERE name = 'Tecnología' LIMIT 1),
   '/tech-conference-modern.jpg', 'published',
   'Centro de Convenciones Córdoba', 'Moderno centro de eventos en Córdoba', 'Av. Figueroa Alcorta 455', 'Córdoba', 'Córdoba', 'Argentina',
   -31.4217, -64.1838, '2025-10-22T09:00:00-03:00', '2025-10-23T18:00:00-03:00', true),

  -- 6: Noche de Tango en San Telmo (10 nov 2025)
  ('e0e0e0e0-0001-4000-a001-000000000006', 'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0',
   'Noche de Tango en San Telmo', 'noche-tango-san-telmo',
   'Show de tango con orquesta típica, parejas de baile y cena de gala.',
   (SELECT id FROM events.categories WHERE name = 'Conciertos' LIMIT 1),
   '/tango-show-buenos-aires.jpg', 'published',
   'El Viejo Almacén', 'Histórica casa de tango de San Telmo', 'Av. Independencia 299', 'Buenos Aires', 'Buenos Aires', 'Argentina',
   -34.6187, -58.3694, '2025-11-10T20:00:00-03:00', '2025-11-10T23:30:00-03:00', true),

  -- 7: Hackathon BA (29 nov 2025)
  ('e0e0e0e0-0001-4000-a001-000000000007', 'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0',
   'Hackathon Buenos Aires 2025', 'hackathon-ba-2025',
   'Maratón de programación de 48 horas con premios para las mejores soluciones tecnológicas.',
   (SELECT id FROM events.categories WHERE name = 'Tecnología' LIMIT 1),
   '/hackathon-coding-event.jpg', 'published',
   'Digital House', 'Campus tecnológico en Belgrano', 'Monroe 860', 'Buenos Aires', 'Buenos Aires', 'Argentina',
   -34.5615, -58.4511, '2025-11-29T09:00:00-03:00', '2025-12-01T18:00:00-03:00', true),

  -- 8: Festival Gastronómico de Fin de Año (15 dic 2025)
  ('e0e0e0e0-0001-4000-a001-000000000008', 'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0',
   'Festival Gastronómico de Fin de Año', 'festival-gastronomico-fin-de-ano',
   'Los mejores chefs argentinos reunidos en un festival culinario con degustación y música en vivo.',
   (SELECT id FROM events.categories WHERE name = 'Gastronomía' LIMIT 1),
   '/gastro-festival-chefs.jpg', 'published',
   'Hipódromo de Palermo', 'Espacio amplio al aire libre en Palermo', 'Av. del Libertador 4101', 'Buenos Aires', 'Buenos Aires', 'Argentina',
   -34.5557, -58.4276, '2025-12-15T12:00:00-03:00', '2025-12-15T23:00:00-03:00', true),

  -- 9: Obra de Teatro: Esperando a Godot (18 ene 2026)
  ('e0e0e0e0-0001-4000-a001-000000000009', 'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0',
   'Esperando a Godot - Revisitado', 'esperando-godot-revisitado',
   'Una reinterpretación contemporánea del clásico de Beckett con puesta en escena innovadora.',
   (SELECT id FROM events.categories WHERE name = 'Teatro' LIMIT 1),
   '/theater-play-stage-performance-dramatic-lighting.jpg', 'published',
   'Teatro Coliseo', 'Teatro clásico de Buenos Aires', 'Marcelo T. de Alvear 1125', 'Buenos Aires', 'Buenos Aires', 'Argentina',
   -34.5973, -58.3820, '2026-01-18T20:00:00-03:00', '2026-01-18T22:30:00-03:00', true),

  -- 10: Concierto Electrónica Under (08 feb 2026)
  ('e0e0e0e0-0001-4000-a001-000000000010', 'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0',
   'Electrónica Under: Warehouse Party', 'electronica-under-warehouse',
   'DJs nacionales e internacionales en un warehouse secreto de Buenos Aires.',
   (SELECT id FROM events.categories WHERE name = 'Festival' LIMIT 1),
   '/electronic-music-warehouse-party.jpg', 'published',
   'Warehouse Dock Sud', 'Espacio industrial reconvertido', 'Av. Regimientos Patricios 1200', 'Buenos Aires', 'Buenos Aires', 'Argentina',
   -34.6389, -58.3567, '2026-02-08T23:00:00-03:00', '2026-02-09T06:00:00-03:00', true)
ON CONFLICT (id) DO NOTHING;

-- ────────────────────────────────────────────────────
-- Áreas para cada evento pasado
-- ────────────────────────────────────────────────────

-- 1: Jazz Palermo
INSERT INTO events.areas (id, event_id, name, is_general_admission, capacity, position) VALUES
  ('b0b00001-0001-4000-b001-000000000001', 'e0e0e0e0-0001-4000-a001-000000000001', 'General', true, 300, 1),
  ('b0b00001-0001-4000-b001-000000000002', 'e0e0e0e0-0001-4000-a001-000000000001', 'VIP Mesa', false, 50, 2)
ON CONFLICT (id) DO NOTHING;

-- 2: Stand-Up Comedy
INSERT INTO events.areas (id, event_id, name, is_general_admission, capacity, position) VALUES
  ('b0b00002-0001-4000-b001-000000000001', 'e0e0e0e0-0001-4000-a001-000000000002', 'Platea', false, 400, 1),
  ('b0b00002-0001-4000-b001-000000000002', 'e0e0e0e0-0001-4000-a001-000000000002', 'Pullman', false, 200, 2)
ON CONFLICT (id) DO NOTHING;

-- 3: Cine Independiente
INSERT INTO events.areas (id, event_id, name, is_general_admission, capacity, position) VALUES
  ('b0b00003-0001-4000-b001-000000000001', 'e0e0e0e0-0001-4000-a001-000000000003', 'Sala Principal', true, 250, 1)
ON CONFLICT (id) DO NOTHING;

-- 4: Taller Fotografía
INSERT INTO events.areas (id, event_id, name, is_general_admission, capacity, position) VALUES
  ('b0b00004-0001-4000-b001-000000000001', 'e0e0e0e0-0001-4000-a001-000000000004', 'Aula Práctica', true, 30, 1)
ON CONFLICT (id) DO NOTHING;

-- 5: DevOps Conference
INSERT INTO events.areas (id, event_id, name, is_general_admission, capacity, position) VALUES
  ('b0b00005-0001-4000-b001-000000000001', 'e0e0e0e0-0001-4000-a001-000000000005', 'Early Bird', true, 200, 1),
  ('b0b00005-0001-4000-b001-000000000002', 'e0e0e0e0-0001-4000-a001-000000000005', 'General', true, 500, 2),
  ('b0b00005-0001-4000-b001-000000000003', 'e0e0e0e0-0001-4000-a001-000000000005', 'VIP Workshop', false, 50, 3)
ON CONFLICT (id) DO NOTHING;

-- 6: Tango Night
INSERT INTO events.areas (id, event_id, name, is_general_admission, capacity, position) VALUES
  ('b0b00006-0001-4000-b001-000000000001', 'e0e0e0e0-0001-4000-a001-000000000006', 'Mesa Estándar', true, 100, 1),
  ('b0b00006-0001-4000-b001-000000000002', 'e0e0e0e0-0001-4000-a001-000000000006', 'Mesa Premium', false, 40, 2)
ON CONFLICT (id) DO NOTHING;

-- 7: Hackathon
INSERT INTO events.areas (id, event_id, name, is_general_admission, capacity, position) VALUES
  ('b0b00007-0001-4000-b001-000000000001', 'e0e0e0e0-0001-4000-a001-000000000007', 'Participante', true, 150, 1)
ON CONFLICT (id) DO NOTHING;

-- 8: Festival Gastronómico Fin de Año
INSERT INTO events.areas (id, event_id, name, is_general_admission, capacity, position) VALUES
  ('b0b00008-0001-4000-b001-000000000001', 'e0e0e0e0-0001-4000-a001-000000000008', 'General', true, 3000, 1),
  ('b0b00008-0001-4000-b001-000000000002', 'e0e0e0e0-0001-4000-a001-000000000008', 'VIP Gourmet', false, 150, 2)
ON CONFLICT (id) DO NOTHING;

-- 9: Teatro Godot
INSERT INTO events.areas (id, event_id, name, is_general_admission, capacity, position) VALUES
  ('b0b00009-0001-4000-b001-000000000001', 'e0e0e0e0-0001-4000-a001-000000000009', 'Platea', false, 300, 1),
  ('b0b00009-0001-4000-b001-000000000002', 'e0e0e0e0-0001-4000-a001-000000000009', 'Super Pullman', false, 150, 2)
ON CONFLICT (id) DO NOTHING;

-- 10: Electrónica Under
INSERT INTO events.areas (id, event_id, name, is_general_admission, capacity, position) VALUES
  ('b0b00010-0001-4000-b001-000000000001', 'e0e0e0e0-0001-4000-a001-000000000010', 'Pista', true, 800, 1),
  ('b0b00010-0001-4000-b001-000000000002', 'e0e0e0e0-0001-4000-a001-000000000010', 'VIP Backstage', false, 80, 2)
ON CONFLICT (id) DO NOTHING;

-- ────────────────────────────────────────────────────
-- Precios por área (idempotente)
-- ────────────────────────────────────────────────────

-- 1: Jazz
INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'b0b00001-0001-4000-b001-000000000001', 1500000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'b0b00001-0001-4000-b001-000000000001');
INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'b0b00001-0001-4000-b001-000000000002', 3500000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'b0b00001-0001-4000-b001-000000000002');

-- 2: Stand-Up
INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'b0b00002-0001-4000-b001-000000000001', 2000000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'b0b00002-0001-4000-b001-000000000001');
INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'b0b00002-0001-4000-b001-000000000002', 1200000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'b0b00002-0001-4000-b001-000000000002');

-- 3: Cine
INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'b0b00003-0001-4000-b001-000000000001', 800000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'b0b00003-0001-4000-b001-000000000001');

-- 4: Taller
INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'b0b00004-0001-4000-b001-000000000001', 2500000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'b0b00004-0001-4000-b001-000000000001');

-- 5: DevOps
INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'b0b00005-0001-4000-b001-000000000001', 1000000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'b0b00005-0001-4000-b001-000000000001');
INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'b0b00005-0001-4000-b001-000000000002', 1800000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'b0b00005-0001-4000-b001-000000000002');
INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'b0b00005-0001-4000-b001-000000000003', 5000000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'b0b00005-0001-4000-b001-000000000003');

-- 6: Tango
INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'b0b00006-0001-4000-b001-000000000001', 2000000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'b0b00006-0001-4000-b001-000000000001');
INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'b0b00006-0001-4000-b001-000000000002', 4500000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'b0b00006-0001-4000-b001-000000000002');

-- 7: Hackathon
INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'b0b00007-0001-4000-b001-000000000001', 500000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'b0b00007-0001-4000-b001-000000000001');

-- 8: Gastro Fin de Año
INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'b0b00008-0001-4000-b001-000000000001', 1200000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'b0b00008-0001-4000-b001-000000000001');
INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'b0b00008-0001-4000-b001-000000000002', 4000000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'b0b00008-0001-4000-b001-000000000002');

-- 9: Teatro Godot
INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'b0b00009-0001-4000-b001-000000000001', 2800000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'b0b00009-0001-4000-b001-000000000001');
INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'b0b00009-0001-4000-b001-000000000002', 1500000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'b0b00009-0001-4000-b001-000000000002');

-- 10: Electrónica Under
INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'b0b00010-0001-4000-b001-000000000001', 1800000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'b0b00010-0001-4000-b001-000000000001');
INSERT INTO events.area_pricing (area_id, price_cents, currency)
SELECT 'b0b00010-0001-4000-b001-000000000002', 5500000, 'ARS'
WHERE NOT EXISTS (SELECT 1 FROM events.area_pricing WHERE area_id = 'b0b00010-0001-4000-b001-000000000002');
