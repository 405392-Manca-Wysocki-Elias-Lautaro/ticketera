INSERT INTO roles (code, name, description) VALUES
    ('SUPER_ADMIN', 'Super Admin', 'Acceso total a la plataforma'),
    ('SUPPORT', 'Soporte', 'Soporte y atención al cliente'),
    ('OWNER', 'Owner', 'Dueño de una organización'),
    ('ADMIN', 'Administrador', 'Administra todos los eventos de la organización'),
    ('MANAGER', 'Manager', 'Gestiona uno o más eventos específicos'),
    ('STAFF', 'Staff', 'Operaciones en el evento como control de accesos'),
    ('CUSTOMER', 'Cliente', 'Usuario que compra tickets')
ON CONFLICT (code) DO NOTHING;
