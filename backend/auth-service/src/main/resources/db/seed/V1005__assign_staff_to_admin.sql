DO $$
DECLARE
    admin_org_id UUID;
    staff_user_email VARCHAR := 'staff@ticketera.com';
    admin_user_email VARCHAR := 'admin@ticketera.com';
BEGIN
    -- 1. Asegurar que admin tenga organization_id
    -- Si ya tiene, lo usa. Si no, genera uno nuevo.
    UPDATE auth.users 
    SET organization_id = gen_random_uuid() 
    WHERE email = admin_user_email AND organization_id IS NULL;

    -- Obtener el organization_id del admin
    SELECT organization_id INTO admin_org_id 
    FROM auth.users 
    WHERE email = admin_user_email;

    IF admin_org_id IS NOT NULL THEN
        -- 2. Asignar ese organization_id al usuario staff
        UPDATE auth.users 
        SET organization_id = admin_org_id 
        WHERE email = staff_user_email;
        
        -- 3. Log de lo que pasó (opcional, solo para depuración si se corre manual)
        RAISE NOTICE 'Updated staff user % with organization_id % from admin %', staff_user_email, admin_org_id, admin_user_email;
    ELSE
        RAISE NOTICE 'Admin user % not found or has no organization_id', admin_user_email;
    END IF;

END $$;
