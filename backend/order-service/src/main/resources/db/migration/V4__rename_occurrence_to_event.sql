-- Renombrar occurrence_id a event_id para mayor claridad
-- occurrence ya no existe como entidad separada, está integrado en events
-- También simplificar nombres de venue_area y venue_seat

-- Renombrar columnas
ALTER TABLE orders.order_items 
RENAME COLUMN occurrence_id TO event_id;

ALTER TABLE orders.order_items 
RENAME COLUMN event_venue_area_id TO venue_area_id;

ALTER TABLE orders.order_items 
RENAME COLUMN event_venue_seat_id TO venue_seat_id;

-- Actualizar índices
DROP INDEX IF EXISTS orders.idx_order_items_occurrence;
CREATE INDEX idx_order_items_event ON orders.order_items(event_id);

-- Actualizar comentarios
COMMENT ON COLUMN orders.order_items.event_id IS 'ID del evento (anteriormente occurrence_id)';
COMMENT ON COLUMN orders.order_items.venue_area_id IS 'ID del área/sector del evento (ej: Campo, Platea)';
COMMENT ON COLUMN orders.order_items.venue_seat_id IS 'ID del asiento específico (opcional, solo para asientos numerados)';

