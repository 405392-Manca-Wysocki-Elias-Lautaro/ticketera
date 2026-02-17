-- Semilla de usuarios iniciales (Admin, Staff, Customer)
-- Todos los usuarios tienen la contraseña: 'password'
-- Hash generado con BCrypt (cost 10): $2a$10$TJ5QlJJXK7SZ/PK07WmdcexOGXJ6z220WlQlC1pxXHBgyAYIawBDS


INSERT INTO auth.users (first_name, last_name, email, email_verified, is_active, password_hash, role_id)
SELECT 'Admin', 'User', 'admin@ticketera.com', TRUE, TRUE, '$2a$10$TJ5QlJJXK7SZ/PK07WmdcexOGXJ6z220WlQlC1pxXHBgyAYIawBDS
', id
FROM auth.roles WHERE code = 'ADMIN'
ON CONFLICT (email) DO NOTHING;

INSERT INTO auth.users (first_name, last_name, email, email_verified, is_active, password_hash, role_id)
SELECT 'Staff', 'User', 'staff@ticketera.com', TRUE, TRUE, '$2a$10$TJ5QlJJXK7SZ/PK07WmdcexOGXJ6z220WlQlC1pxXHBgyAYIawBDS
', id
FROM auth.roles WHERE code = 'STAFF'
ON CONFLICT (email) DO NOTHING;

INSERT INTO auth.users (first_name, last_name, email, email_verified, is_active, password_hash, role_id)
SELECT 'Customer', 'User', 'user@ticketera.com', TRUE, TRUE, '$2a$10$TJ5QlJJXK7SZ/PK07WmdcexOGXJ6z220WlQlC1pxXHBgyAYIawBDS
', id
FROM auth.roles WHERE code = 'CUSTOMER'
ON CONFLICT (email) DO NOTHING;
