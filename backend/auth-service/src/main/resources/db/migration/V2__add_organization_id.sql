ALTER TABLE auth.users ADD COLUMN organization_id UUID;
CREATE INDEX idx_users_organization_id ON auth.users(organization_id);
