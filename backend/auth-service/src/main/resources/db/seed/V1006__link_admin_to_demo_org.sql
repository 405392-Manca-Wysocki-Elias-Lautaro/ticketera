UPDATE auth.users 
SET organization_id = 'a1b2c3d4-e5f6-7890-abcd-ef1234567890'
WHERE email IN ('admin@ticketera.com', 'staff@ticketera.com');
