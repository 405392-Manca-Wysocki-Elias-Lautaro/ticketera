# Ticketera Database Documentation

## Overview
La base de datos de **Ticketera** está organizada en **6 schemas**:

- `auth`: Usuarios, roles, organizadores, membresías y claves API.
- `events`: Eventos, funciones (occurrences), venues, áreas y precios.
- `orders`: Clientes, órdenes y sus ítems.
- `tickets`: Tickets emitidos, historial y reservas temporales (holds).
- `payments`: Métodos de pago, intents, capturas y reembolsos.
- `notifications`: Tablas outbox para publicación de eventos.

---

## Timestamps y Soft Delete
Todas las entidades principales incluyen:
- `created_at`: fecha de creación.
- `updated_at`: actualizado automáticamente con triggers.
- `deleted_at`: soft delete (NULL = activo, NOT NULL = borrado lógico).

Las tablas históricas (`*_history`) y `outbox` **no incluyen deleted_at** ya que son inmutables.

---

## Schemas

### 1. Auth
- **users**: credenciales y datos de usuarios del sistema.
- **roles**: roles predefinidos (`admin`, `manager`, `seller`, etc.).
- **organizers**: organizadores de eventos.
- **categories**: categorías de eventos.
- **organizer_memberships**: qué usuarios pertenecen a qué organizador y con qué rol.
- **api_keys**: claves para apps externas (POS, check-in apps).

### 2. Events
- **events**: un evento base (ej. "Lollapalooza 2025").
- **event_occurrences**: cada función/fecha del evento (ej. "Día 1 - 10/03/2025").
- **venues**: lugares físicos (ej. "Estadio River").
- **event_venue_areas**: áreas de un venue adaptadas a una occurrence (ej. "Campo VIP" solo en ese show).
- **event_venue_seats**: butacas concretas por occurrence (para plateas numeradas).
- **seat_types**: clasificaciones de asientos (VIP, platea, campo).
- **ticket_types**: tipos de ticket (adulto, niño, promo).
- **event_media**: imágenes asociadas al evento.
- **event_grants**: permisos granulares para miembros sobre eventos.
- **area_allocations**: cupo disponible por área y función.
- **prices**: precio por combinación de occurrence + área/asiento/tipo.

### 3. Orders
- **customers**: clientes compradores.
- **orders**: órdenes generadas por clientes.
- **order_items**: tickets o asientos dentro de una orden.
- **order_status_history**: historial de cambios de estado en la orden.

### 4. Tickets
- **tickets**: tickets emitidos con código único (QR/UUID).
- **ticket_status_history**: historial de cambios de estado.
- **holds**: reservas temporales para prevenir sobreventa.

### 5. Payments
- **payment_providers**: métodos de pago soportados (ej. MercadoPago, Stripe).
- **payment_intents**: intención de pago creada para una orden.
- **payment_captures**: confirmación de captura de dinero.
- **refunds**: solicitudes de devolución.
- **refund_items**: detalle de qué ítems/tickets fueron reembolsados.
- **refund_policies**: políticas de reembolso por organizador/evento.

### 6. Notifications (Outbox)
- **tickets_outbox**: eventos relacionados con tickets y órdenes (ej. `TicketIssued`, `OrderCreated`).
- **payments_outbox**: eventos relacionados con pagos (`PaymentSucceeded`, `RefundProcessed`).

Estas tablas implementan el **Transactional Outbox Pattern**:
- Los cambios de dominio y la escritura del evento ocurren en la misma transacción.
- Los eventos pendientes (`published_at IS NULL`) son procesados por un worker y publicados en RabbitMQ/Kafka.
- Una vez publicados, se marca `published_at` para evitar duplicados.

---

## Relaciones principales

- `auth.users` ↔ `orders.customers` (un usuario puede ser cliente).
- `auth.organizers` ↔ `events.events` (un organizador crea eventos).
- `events.events` ↔ `events.event_occurrences` (un evento puede tener múltiples funciones).
- `events.event_occurrences` ↔ `events.event_venue_areas` (las áreas del venue se definen por función).
- `orders.orders` ↔ `orders.order_items` ↔ `tickets.tickets`.
- `orders.orders` ↔ `payments.payment_intents` ↔ `payments.payment_captures`.
- `orders.orders` ↔ `payments.refunds`.

---

## Restricciones y Checks

- Estados (`status`) tienen `CHECK` constraints en `events.events`, `orders.orders`, `tickets.tickets`, `payments.payment_intents`, `payments.refunds`.
- `area_allocations.sold <= capacity` asegura no sobrepasar cupo.
- `tickets` asegura unicidad de `code`.
- `holds` asegura que no existan reservas activas duplicadas para un mismo asiento.
- Soft delete implementado en todas las entidades principales.

---

## Triggers
- **`updated_at`**: cada tabla principal tiene un trigger `BEFORE UPDATE` que setea automáticamente `updated_at = now()`.  
- Opcional: podrían añadirse triggers de auditoría para registrar cambios en logs.

---

# Ejemplo de Flujo

1. Cliente compra un ticket:  
   - Se crea `orders.orders` y `orders.order_items`.  
   - Se generan `tickets.tickets` asociados.  
   - Se crea `payments.payment_intents`.  
   - En la misma transacción se inserta un registro en `payments_outbox` (`PaymentRequested`).  

2. Worker publica en Kafka:  
   - Lee `payments_outbox` donde `published_at IS NULL`.  
   - Publica el evento.  
   - Actualiza `published_at`.  

---
