INSERT INTO users (
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
    'Elias', 
    'Manca', 
    'eliasmanca20@gmail.com',
    '$2a$10$e7SCP0dxF.DSkZo0dkCy1ehmg.8ff7CLEHUrK7KefEqScKKlZZWpW', 
    (SELECT id FROM roles WHERE code = 'SUPER_ADMIN'),
    true,
    true, 
    now()
);