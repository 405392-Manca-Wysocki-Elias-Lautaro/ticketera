-- Asignar los 3 staffs (el original + 2 nuevos) a los eventos del Admin

INSERT INTO events.event_staff (event_id, user_id, assigned_at)
SELECT 
  e.id, 
  u.id, 
  NOW()
FROM events.events e
CROSS JOIN (
  VALUES 
    ('a0a0a0a0-a0a0-a0a0-a0a0-a0a0a0a0a0a0'::uuid), -- Staff Original
    ('d0d0d0d0-d0d0-d0d0-d0d0-d0d0d0d0d0d1'::uuid), -- Staff 1
    ('d0d0d0d0-d0d0-d0d0-d0d0-d0d0d0d0d0d2'::uuid)  -- Staff 2
) AS u(id)
WHERE e.organizer_id = 'c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0' -- Eventos del Admin
ON CONFLICT (event_id, user_id) DO NOTHING;
