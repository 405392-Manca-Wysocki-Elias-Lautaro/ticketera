INSERT INTO events.organizers (name, slug, contact_email, phone_number)
VALUES 
  ('Ultimate Fighting Championship', 'ufc-345', 'contact@ufc.com', '17025551234'),
  ('Lollapalooza Argentina', 'lolla-arg-2025', 'info@lollapalooza.com.ar', '541144445678');

INSERT INTO events.venues (organizer_id, name, description, address_line, city, state, country, lat, lng) 
VALUES 
  (1, 'Estadio Monumental', 'Estadio Monumental de Núñez', 'Av. Monseñor José Félix Uriburu 1234', 'Buenos Aires', 'Buenos Aires', 'Argentina', -34.603684, -58.381559),
  (2, 'Estadio Mario Alberto Kempes', 'Estadio de Córdoba', 'Avenida Ramón Cárcano', 'Córdoba', 'Córdoba', 'Argentina', -31.4427, -64.2356);

INSERT INTO events.categories (name, description, active)
VALUES 
  ('Conciertos', 'Eventos musicales en vivo de diferentes géneros y artistas.', true),
  ('Deportes', 'Eventos deportivos profesionales y amateurs.', true);

INSERT INTO events.events (organizer_id, title, slug, description, category_id, cover_url, status, active)
 VALUES 
 (1, 'Show de Fito Páez en el Monumento a la Bandera', 'fito-paez-monumento-rosario', 'Concierto especial de Fito Páez celebrando los 30 años de "El amor después del amor".', 1, 'https://example.com/images/fito-paez.jpg', 'published', true),
 (1, 'Partido Newell’s vs Rosario Central', 'newells-vs-central', 'Clásico rosarino en el Estadio Marcelo Bielsa, fecha 12 del torneo local.', 2, 'https://example.com/images/newells-central.jpg', 'published', true);
