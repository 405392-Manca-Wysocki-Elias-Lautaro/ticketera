CREATE TABLE events.event_staff (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id UUID NOT NULL REFERENCES events.events(id) ON DELETE CASCADE,
    user_id UUID NOT NULL,
    assigned_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    assigned_by UUID,
    CONSTRAINT uq_event_staff UNIQUE (event_id, user_id)
);

CREATE INDEX idx_event_staff_event_id ON events.event_staff(event_id);
CREATE INDEX idx_event_staff_user_id ON events.event_staff(user_id);
