-- Insert new categories
INSERT INTO events.categories (name, description)
VALUES
    ('Teatro', 'Obras de teatro, dramas, comedias y artes escénicas.'),
    ('Conferencia', 'Charlas, seminarios, congresos y eventos educativos.'),
    ('Festival', 'Festivales de música, arte, comida y cultura.'),
    ('Comedia', 'Shows de stand-up, humoristas y espectáculos cómicos.'),
    ('Cine', 'Proyecciones de películas, estrenos y festivales de cine.'),
    ('Taller', 'Actividades prácticas, cursos y talleres educativos.'),
    ('Otro', 'Otros tipos de eventos.')
ON CONFLICT (name) DO NOTHING;
