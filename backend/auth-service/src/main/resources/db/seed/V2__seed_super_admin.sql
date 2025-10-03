INSERT INTO users (email, password_hash, email_verified, is_active, created_at)
VALUES ('eliasmanca20@gmail.com', '$2a$10$WWGU6Iju3fzygH/TooGX9OULCP8XRye7jT4CJMebYDg.7VqICukxi', true, true, now());

INSERT INTO user_roles (user_id, role_id, organizer_id)
VALUES (
    (SELECT id FROM users WHERE email = 'eliasmanca20@gmail.com'),
    (SELECT id FROM roles WHERE code = 'super_admin'),
    NULL
);
