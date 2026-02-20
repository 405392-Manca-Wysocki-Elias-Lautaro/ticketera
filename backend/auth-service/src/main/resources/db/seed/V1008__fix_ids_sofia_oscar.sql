-- Fijar IDs deterministas para Sofia y Oscar para poder asignarles tickets en ticket-service

-- Sofia Enamorado: e0e0e0e0-e0e0-e0e0-e0e0-e0e0e0e0e0e1
UPDATE auth.users
SET id = 'e0e0e0e0-e0e0-e0e0-e0e0-e0e0e0e0e0e1'
WHERE email = 'senamorado@ticketly.com';

-- Oscar Botta: e0e0e0e0-e0e0-e0e0-e0e0-e0e0e0e0e0e2
UPDATE auth.users
SET id = 'e0e0e0e0-e0e0-e0e0-e0e0-e0e0e0e0e0e2'
WHERE email = 'obotta@ticketly.com';
