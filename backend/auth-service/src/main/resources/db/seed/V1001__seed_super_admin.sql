-- Crear SUPER_ADMIN con ID fijo para que coincida con el organizer_id usado en los seed data
INSERT INTO users (
    id,
    first_name,
    last_name, 
    email, 
    password_hash, 
    role_id, 
    email_verified, 
    is_active, 
    created_at
)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'Elias', 
    'Manca', 
    'eliasmanca20@gmail.com',
    '$2a$10$e7SCP0dxF.DSkZo0dkCy1ehmg.8ff7CLEHUrK7KefEqScKKlZZWpW', 
    (SELECT id FROM roles WHERE code = 'SUPER_ADMIN'),
    true,
    true, 
    now()
)
ON CONFLICT (id) DO UPDATE
SET 
    first_name = EXCLUDED.first_name,
    last_name = EXCLUDED.last_name,
    email = EXCLUDED.email,
    password_hash = EXCLUDED.password_hash,
    role_id = EXCLUDED.role_id,
    email_verified = EXCLUDED.email_verified,
    is_active = EXCLUDED.is_active;