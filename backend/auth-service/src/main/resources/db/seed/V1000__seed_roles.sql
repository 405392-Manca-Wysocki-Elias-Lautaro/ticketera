INSERT INTO roles (code, name, description) VALUES
    ('SUPER_ADMIN', 'Super Admin', 'Acceso total a la plataforma'),
    ('OWNER', 'Owner', 'Dueño de una organización'),
    ('ADMIN', 'Administrador', 'Administra todos los eventos de la organización'),
    ('STAFF', 'Staff', 'Operaciones en el evento como control de accesos'),
    ('CUSTOMER', 'Cliente', 'Usuario que compra tickets')
ON CONFLICT (code) DO NOTHING;
