INSERT INTO roles (code, name, description) VALUES
    ('super_admin', 'Super Admin', 'Acceso total a la plataforma'),
    ('support', 'Soporte', 'Soporte y atención al cliente'),
    ('owner', 'Owner', 'Dueño de una organización'),
    ('admin', 'Administrador', 'Administra todos los eventos de la organización'),
    ('manager', 'Manager', 'Gestiona uno o más eventos específicos'),
    ('staff', 'Staff', 'Operaciones en el evento como control de accesos'),
    ('customer', 'Cliente', 'Usuario que compra tickets');
ON CONFLICT (code) DO NOTHING;
