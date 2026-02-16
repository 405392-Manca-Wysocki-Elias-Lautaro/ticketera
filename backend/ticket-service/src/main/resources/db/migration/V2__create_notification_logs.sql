CREATE TABLE tickets.notification_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id UUID NOT NULL,
    reminder_type VARCHAR(255) NOT NULL,
    sent_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    status VARCHAR(50),
    CONSTRAINT uk_ticket_reminder_type UNIQUE (ticket_id, reminder_type)
);
