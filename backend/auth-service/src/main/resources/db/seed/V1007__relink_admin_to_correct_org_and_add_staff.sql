-- 1. Actualizar Admin y Staff existente para que apunten a la organización correcta y tengan nombres reales
-- Esta organización 'Admin User' ya existe en los seeds de event-service
UPDATE auth.users 
SET 
  organization_id = 'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0',
  first_name = 'Juan',
  last_name = 'Pérez'
WHERE email = 'admin@ticketly.com';

UPDATE auth.users 
SET 
  organization_id = 'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0',
  first_name = 'Lucía',
  last_name = 'Gómez'
WHERE email = 'staff@ticketly.com';

-- 2. Crear 2 nuevos usuarios Staff asignados a la misma organización con nombres reales
INSERT INTO auth.users (id, first_name, last_name, email, email_verified, is_active, password_hash, role_id, organization_id)
SELECT 
  'd0d0d0d0-d0d0-d0d0-d0d0-d0d0d0d0d0d1', -- ID Staff 1
  'Marcos', 
  'Rodríguez', 
  'staff1@ticketly.com', 
  TRUE, 
  TRUE, 
  '$2a$10$16jvhDx/5BGlTyrlY3rOfeLSkSxnKSHCjKGhmmSZ00zFk9YB2dT2u', -- Password123$
  id, -- Role ID de STAFF
  'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0'
FROM auth.roles WHERE code = 'STAFF'
ON CONFLICT (email) DO NOTHING;

INSERT INTO auth.users (id, first_name, last_name, email, email_verified, is_active, password_hash, role_id, organization_id)
SELECT 
  'd0d0d0d0-d0d0-d0d0-d0d0-d0d0d0d0d0d2', -- ID Staff 2
  'Valentina', 
  'Fernández', 
  'staff2@ticketly.com', 
  TRUE, 
  TRUE, 
  '$2a$10$16jvhDx/5BGlTyrlY3rOfeLSkSxnKSHCjKGhmmSZ00zFk9YB2dT2u', -- Password123$
  id, -- Role ID de STAFF
  'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0'
FROM auth.roles WHERE code = 'STAFF'
ON CONFLICT (email) DO NOTHING;
