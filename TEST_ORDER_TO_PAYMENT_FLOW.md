# Test del Flujo Completo: Order-Service → Payment-Service → Mercado Pago

## ✅ Cambios Aplicados para el Flujo Completo

### 1. **Payment-Service**
- ✅ Endpoint corregido: `POST /intents` (sin prefijo adicional)
- ✅ `PaymentIntentResponse` con helpers compatibles con `order-service`
- ✅ Status devuelto en mayúsculas (PENDING, CAPTURED, etc.)

### 2. **Order-Service**
- ✅ URL del payment-service corregida: `http://payment-dev:8080`
- ✅ `PaymentServiceClient` ya configurado para llamar a `/intents`
- ✅ Flujo completo implementado en `OrderService.createOrder()`

---

## 🚀 Pasos para Probar

### 1. Levantar los Servicios

```bash
# Levantar PostgreSQL, RabbitMQ y ambos servicios
docker-compose up postgres rabbitmq payment-dev order-dev -d

# Ver logs en tiempo real
docker-compose logs -f payment-dev order-dev
```

### 2. Verificar que Ambos Servicios Estén Funcionando

```bash
# Health check del payment-service
curl http://localhost:8081/health

# Health check del order-service
curl http://localhost:8087/health
```

---

## 📝 CURL para Crear una Orden (Flujo Completo)

Este comando creará una orden en el `order-service`, que automáticamente:
1. Guardará la orden en la BD
2. Llamará al `payment-service`
3. Creará una preferencia en Mercado Pago
4. Devolverá la orden con la información del pago

```bash
curl -X POST http://localhost:8087/create \
  -H "Content-Type: application/json" \
  -d '{
    "customer": {
      "email": "juan.perez@example.com",
      "firstName": "Juan",
      "lastName": "Pérez",
      "phone": "+5491123456789"
    },
    "organizerId": 1,
    "currency": "ARS",
    "notes": "Prueba de integración con Mercado Pago",
    "items": [
      {
        "occurrenceId": 1,
        "ticketTypeId": 1,
        "unitPriceCents": 50000,
        "quantity": 2
      }
    ]
  }'
```

### Parámetros Explicados:

- **`occurrenceId`**: ID de la ocurrencia del evento (debe existir en la BD)
- **`ticketTypeId`**: ID del tipo de ticket (debe existir en la BD)
- **`unitPriceCents`**: Precio en centavos (50000 = $500 ARS)
- **`quantity`**: Cantidad de tickets
- **`organizerId`**: ID del organizador (debe existir en la BD)

---

## 📊 Respuesta Esperada

```json
{
  "id": 1,
  "organizerId": 1,
  "customer": {
    "id": 1,
    "email": "juan.perez@example.com",
    "fullName": "Juan Pérez",
    "phone": "+5491123456789"
  },
  "status": "PENDING",
  "totalCents": 100000,
  "currency": "ARS",
  "paymentRef": null,
  "items": [
    {
      "id": 1,
      "occurrenceId": 1,
      "ticketTypeId": 1,
      "unitPriceCents": 50000,
      "quantity": 2,
      "totalCents": 100000
    }
  ],
  "createdAt": "2024-11-09T18:30:00",
  "updatedAt": "2024-11-09T18:30:00"
}
```

**NOTA:** La respuesta NO incluirá la URL de Mercado Pago directamente en la orden. Necesitarías consultar el payment-service o modificar el `OrderResponse` para incluir la URL.

---

## 🔍 Verificar que Funcionó

### 1. Verificar en la Base de Datos (Payment-Service)

```bash
docker exec -it ticketera-postgres-dev psql -U ticketera_app -d ticketera_dev -c "SELECT id, order_id, status, payment_url FROM payments.payment_intents ORDER BY id DESC LIMIT 1;"
```

**Deberías ver:**
- Un registro con `status = 'pending'`
- Una `payment_url` que empiece con `https://www.mercadopago.com.ar/checkout/...`

### 2. Verificar en la Base de Datos (Order-Service)

```bash
docker exec -it ticketera-postgres-dev psql -U ticketera_app -d ticketera_dev -c "SELECT id, status, total_cents, currency FROM orders.orders ORDER BY id DESC LIMIT 1;"
```

**Deberías ver:**
- Un registro con la orden creada
- `status = 'pending'` o el estado correspondiente

### 3. Verificar Logs

```bash
# Logs del payment-service
docker-compose logs payment-dev | grep "Creating payment intent"
docker-compose logs payment-dev | grep "Payment intent created successfully"

# Logs del order-service
docker-compose logs order-dev | grep "Creating new order"
docker-compose logs order-dev | grep "Processing payment"
docker-compose logs order-dev | grep "Order created successfully"
```

---

## ⚠️ Posibles Problemas

### Problema: "Connection refused" al payment-service

**Causa:** Los servicios no están en la misma red de Docker o el payment-service no está corriendo.

**Solución:**
```bash
# Verificar que ambos estén en backend_net
docker network inspect ticketera_backend_net

# Verificar que payment-dev esté corriendo
docker ps | grep payment-dev
```

### Problema: "Foreign key constraint violation - provider_id"

**Causa:** No existe el proveedor de pago con ID=1 en la tabla `payment_providers`.

**Solución:** La migración V3 debería haberlo creado. Verificar:
```bash
docker exec -it ticketera-postgres-dev psql -U ticketera_app -d ticketera_dev -c "SELECT * FROM payments.payment_providers;"
```

Si no existe, insertarlo manualmente:
```sql
INSERT INTO payments.payment_providers (id, code, name, created_at, updated_at)
VALUES (1, 'mercadopago', 'Mercado Pago', now(), now());
```

### Problema: "Foreign key constraint - occurrenceId o ticketTypeId no existen"

**Causa:** Los IDs en el request no existen en las tablas de eventos/tickets.

**Solución:** Usar IDs que existan realmente en tu BD, o crear datos de prueba primero.

---

## 🎯 Próximo Paso: Obtener la URL de Mercado Pago

Actualmente, el `OrderResponse` NO incluye la URL de pago de Mercado Pago. Hay dos opciones:

### Opción 1: Modificar OrderResponse para incluir paymentUrl

Agregar un campo `paymentUrl` al `OrderResponse` que se obtenga del `PaymentResponse`.

### Opción 2: Consultar el Payment-Service Directamente

```bash
# Obtener el payment_intent_id de la orden
PAYMENT_ID=$(docker exec -it ticketera-postgres-dev psql -U ticketera_app -d ticketera_dev -t -c "SELECT id FROM payments.payment_intents WHERE order_id = 1;")

# Consultar el payment-service
curl http://localhost:8081/intents/$PAYMENT_ID
```

---

## 📋 Checklist de Verificación

- [ ] PostgreSQL corriendo
- [ ] RabbitMQ corriendo
- [ ] Payment-service corriendo en puerto 8081
- [ ] Order-service corriendo en puerto 8087
- [ ] Tabla `payment_providers` tiene registro con ID=1
- [ ] Existen datos de prueba (organizerId, occurrenceId, ticketTypeId)
- [ ] Los servicios están en la misma red Docker (`backend_net`)
- [ ] Las migraciones de Flyway se ejecutaron correctamente

---

## 🔧 Comandos Útiles

```bash
# Ver todos los contenedores corriendo
docker-compose ps

# Reiniciar solo el payment-service
docker-compose restart payment-dev

# Reiniciar solo el order-service
docker-compose restart order-dev

# Ver logs con timestamps
docker-compose logs --timestamps -f payment-dev order-dev

# Limpiar todo y empezar de cero
docker-compose down -v
docker-compose up postgres rabbitmq payment-dev order-dev -d
```

---

## ✅ Si Todo Funciona Correctamente

Deberías ver en los logs:

```
order-dev      | Creating new order for customer: juan.perez@example.com
order-dev      | Processing payment for order: 1
order-dev      | Creating payment intent for order: 1
payment-dev    | Received payment intent request for order: 1
payment-dev    | Creating payment intent for order: 1
payment-dev    | Creating Mercado Pago preference for order: 1
payment-dev    | Payment intent created successfully: 1 with preference ID: 787997534-...
order-dev      | Payment intent created successfully: 1
order-dev      | Order created successfully: 1
```

Y en la base de datos verás:
- Una orden en `orders.orders` con `status='pending'`
- Un payment intent en `payments.payment_intents` con `status='pending'` y una `payment_url`

🎉 **¡Listo! El flujo completo está funcionando!**

