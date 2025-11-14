-- V5__change_user_id_to_varchar.sql
-- Cambiar el tipo de user_id de BIGINT a VARCHAR(36) para soportar UUID desde auth-service

-- Primero eliminar el índice existente si existe
DROP INDEX IF EXISTS orders.idx_customers_user_id;

-- Cambiar el tipo de columna user_id a VARCHAR(36) para almacenar UUIDs
ALTER TABLE orders.customers 
    ALTER COLUMN user_id TYPE VARCHAR(36);

-- Recrear el índice
CREATE INDEX idx_customers_user_id ON orders.customers(user_id) WHERE deleted_at IS NULL;

-- Agregar comentario
COMMENT ON COLUMN orders.customers.user_id IS 'UUID del usuario desde auth-service';

