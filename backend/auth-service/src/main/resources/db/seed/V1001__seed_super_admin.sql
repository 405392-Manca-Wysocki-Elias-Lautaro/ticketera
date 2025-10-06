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
    '$2a$10$WWGU6Iju3fzygH/TooGX9OULCP8XRye7jT4CJMebYDg.7VqICukxi', 
    (SELECT id FROM roles WHERE code = 'super_admin'),
    true,
    true, 
    now()
);