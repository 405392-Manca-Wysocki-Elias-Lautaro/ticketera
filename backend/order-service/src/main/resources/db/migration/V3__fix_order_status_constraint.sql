-- Actualizar la restricción de status para aceptar valores en mayúsculas
ALTER TABLE orders.orders DROP CONSTRAINT ck_orders_status;

ALTER TABLE orders.orders ADD CONSTRAINT ck_orders_status 
CHECK (status IN ('pending','paid','failed','canceled','refunded','partially_refunded',
                  'PENDING','PAID','FAILED','CANCELED','REFUNDED','PARTIALLY_REFUNDED'));
