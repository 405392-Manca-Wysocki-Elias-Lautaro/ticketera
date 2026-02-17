-- Asigna eventos al Staff
-- ID Staff: a0a0a0a0-a0a0-a0a0-a0a0-a0a0a0a0a0a0
-- Evento 1 (Fito Paez): e5f6a7b8-c9d0-1234-efab-567890123456
-- Evento 2 (Newell's vs Central): f6a7b8c9-d0e1-2345-fabc-678901234567

INSERT INTO events.event_staff (event_id, user_id, assigned_at)
VALUES
  ('e5f6a7b8-c9d0-1234-efab-567890123456', 'a0a0a0a0-a0a0-a0a0-a0a0-a0a0a0a0a0a0', NOW()),
  ('f6a7b8c9-d0e1-2345-fabc-678901234567', 'a0a0a0a0-a0a0-a0a0-a0a0-a0a0a0a0a0a0', NOW())
ON CONFLICT (event_id, user_id) DO NOTHING;
