INSERT INTO events.organizers (name, slug, contact_email, phone_number)
VALUES 
('Ultimate Fighting Championship', 'ufc-345', 'contact@ufc.com', '17025551234');

INSERT INTO events.organizers (name, slug, contact_email, phone_number)
VALUES 
('Lollapalooza Argentina', 'lolla-arg-2025', 'info@lollapalooza.com.ar', '541144445678');

INSERT INTO events.venues (organizer_id, name, description, address_line, city, state, country, lat, lng)
VALUES 
(1, 'Estadio Monumental', 'Estadio Monumental de Núñez', 'Av. Monseñor José Félix Uriburu 1234', 'Buenos Aires', 'Buenos Aires', 'Argentina', -34.603684, -58.381559);

INSERT INTO events.venues (organizer_id, name, description, address_line, city, state, country, lat, lng)
VALUES 
(2, 'Estadio Mario Alberto Kempe', 'Estadio de Córdoba', 'Avenida Ramón Cárcano', 'Córdoba', 'Córdoba', 'Argentina', -34.603684, -58.381559);