-- Asigna el Staff a la organización del Super Admin (Organizer)
-- ID Staff: a0a0a0a0-a0a0-a0a0-a0a0-a0a0a0a0a0a0
-- ID Org (Super Admin): a1b2c3d4-e5f6-7890-abcd-ef1234567890

UPDATE auth.users
SET organization_id = 'a1b2c3d4-e5f6-7890-abcd-ef1234567890'
WHERE id = 'a0a0a0a0-a0a0-a0a0-a0a0-a0a0a0a0a0a0';
