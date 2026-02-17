-- Actualizar IDs de usuarios para que sean deterministas
-- Admin: c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0
-- Staff: a0a0a0a0-a0a0-a0a0-a0a0-a0a0a0a0a0a0
-- Customer: b0b0b0b0-b0b0-b0b0-b0b0-b0b0b0b0b0b0

UPDATE auth.users
SET id = 'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0'
WHERE email = 'admin@ticketera.com';

UPDATE auth.users
SET id = 'a0a0a0a0-a0a0-a0a0-a0a0-a0a0a0a0a0a0'
WHERE email = 'staff@ticketera.com';

UPDATE auth.users
SET id = 'b0b0b0b0-b0b0-b0b0-b0b0-b0b0b0b0b0b0'
WHERE email = 'user@ticketera.com';
