-- Semilla de usuarios iniciales (Admin, Staff, Customer)
-- Todos los usuarios tienen la contraseña: 'Password123$'

INSERT INTO auth.users (first_name, last_name, email, email_verified, is_active, password_hash, role_id)
SELECT 'Admin', 'User', 'admin@ticketly.com', TRUE, TRUE, '$2a$10$16jvhDx/5BGlTyrlY3rOfeLSkSxnKSHCjKGhmmSZ00zFk9YB2dT2u', id
FROM auth.roles WHERE code = 'ADMIN'
ON CONFLICT (email) DO NOTHING;

INSERT INTO auth.users (first_name, last_name, email, email_verified, is_active, password_hash, role_id)
SELECT 'Staff', 'User', 'staff@ticketly.com', TRUE, TRUE, '$2a$10$16jvhDx/5BGlTyrlY3rOfeLSkSxnKSHCjKGhmmSZ00zFk9YB2dT2u', id
FROM auth.roles WHERE code = 'STAFF'
ON CONFLICT (email) DO NOTHING;

INSERT INTO auth.users (first_name, last_name, email, email_verified, is_active, password_hash, role_id)
SELECT 'Customer', 'User', 'user@ticketly.com', TRUE, TRUE, '$2a$10$16jvhDx/5BGlTyrlY3rOfeLSkSxnKSHCjKGhmmSZ00zFk9YB2dT2u', id
FROM auth.roles WHERE code = 'CUSTOMER'
ON CONFLICT (email) DO NOTHING;

INSERT INTO auth.users (first_name, last_name, email, email_verified, is_active, password_hash, role_id)
SELECT 'Sofia', 'Enamorado', 'senamorado@ticketly.com', TRUE, TRUE, '$2a$10$16jvhDx/5BGlTyrlY3rOfeLSkSxnKSHCjKGhmmSZ00zFk9YB2dT2u', id
FROM auth.roles WHERE code = 'CUSTOMER'
ON CONFLICT (email) DO NOTHING;

INSERT INTO auth.users (first_name, last_name, email, email_verified, is_active, password_hash, role_id)
SELECT 'Oscar', 'Botta', 'obotta@ticketly.com', TRUE, TRUE, '$2a$10$16jvhDx/5BGlTyrlY3rOfeLSkSxnKSHCjKGhmmSZ00zFk9YB2dT2u', id
FROM auth.roles WHERE code = 'CUSTOMER'
ON CONFLICT (email) DO NOTHING;
