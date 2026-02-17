-- Seed Admin, Staff, and Customer as Organizers to satisfy FK constraints
INSERT INTO events.organizers (id, name, slug, contact_email, phone_number, active)
VALUES
  ('c0c0c0c0-c0c0-c0c0-c0c0-c0c0c0c0c0c0', 'Admin User', 'admin-user', 'admin@ticketera.com', '1234567890', true),
  ('a0a0a0a0-a0a0-a0a0-a0a0-a0a0a0a0a0a0', 'Staff User', 'staff-user', 'staff@ticketera.com', '1234567890', true),
  ('b0b0b0b0-b0b0-b0b0-b0b0-b0b0b0b0b0b0', 'Customer User', 'customer-user', 'user@ticketera.com', '1234567890', true)
ON CONFLICT (id) DO NOTHING;
