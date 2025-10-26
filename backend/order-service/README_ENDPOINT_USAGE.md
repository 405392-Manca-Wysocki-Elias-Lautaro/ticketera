# Order Service - Endpoint de Registro de Compra

## 📋 Descripción

Este documento describe cómo usar el endpoint de registro de compra implementado en el order-service.

## 🚀 Endpoint Principal

### POST /api/orders

Crea una nueva orden de compra y procesa el pago automáticamente.

#### Request Body

```json
{
  "customer": {
    "email": "cliente@ejemplo.com",
    "firstName": "Juan",
    "lastName": "Pérez",
    "phone": "+5491123456789",
    "userId": 123
  },
  "organizerId": 456,
  "items": [
    {
      "occurrenceId": 789,
      "eventVenueAreaId": 101,
      "eventVenueSeatId": 202,
      "ticketTypeId": 303,
      "unitPriceCents": 5000,
      "quantity": 1
    }
  ],
  "currency": "ARS",
  "notes": "Compra para evento especial",
  "externalReference": "REF-2024-001",
  "expiresAt": "2024-12-31T23:59:59"
}
```

#### Response (201 Created)

```json
{
  "id": 1,
  "customer": {
    "id": 1,
    "email": "cliente@ejemplo.com",
    "firstName": "Juan",
    "lastName": "Pérez",
    "phone": "+5491123456789",
    "userId": 123
  },
  "organizerId": 456,
  "status": "PAID",
  "totalCents": 5000,
  "currency": "ARS",
  "expiresAt": "2024-12-31T23:59:59",
  "paymentMethod": "CREDIT_CARD",
  "notes": "Compra para evento especial",
  "externalReference": "REF-2024-001",
  "createdAt": "2024-10-25T15:30:00",
  "updatedAt": "2024-10-25T15:30:05",
  "paidAt": "2024-10-25T15:30:05",
  "items": [
    {
      "id": 1,
      "occurrenceId": 789,
      "eventVenueAreaId": 101,
      "eventVenueSeatId": 202,
      "ticketTypeId": 303,
      "unitPriceCents": 5000,
      "quantity": 1,
      "totalPriceCents": 5000
    }
  ]
}
```

## 🔍 Otros Endpoints

### GET /api/orders/{orderId}

Obtiene una orden por su ID.

#### Response (200 OK)

```json
{
  "id": 1,
  "customer": { ... },
  "status": "PAID",
  "totalCents": 5000,
  ...
}
```

### GET /api/orders/customer/{customerId}

Obtiene todas las órdenes de un cliente.

#### Response (200 OK)

```json
[
  {
    "id": 1,
    "customer": { ... },
    "status": "PAID",
    ...
  },
  {
    "id": 2,
    "customer": { ... },
    "status": "PENDING",
    ...
  }
]
```

### POST /api/orders/{orderId}/cancel

Cancela una orden existente.

#### Request Body (opcional)

```json
{
  "reason": "Cliente solicitó cancelación"
}
```

#### Response (200 OK)

```
(Sin contenido)
```

## 🎯 Ejemplos de Uso con cURL

### 1. Crear Orden Simple

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customer": {
      "email": "test@ejemplo.com",
      "firstName": "Test",
      "lastName": "User"
    },
    "organizerId": 1,
    "items": [
      {
        "occurrenceId": 1,
        "ticketTypeId": 1,
        "unitPriceCents": 2500,
        "quantity": 2
      }
    ]
  }'
```

### 2. Crear Orden con Asiento Específico

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customer": {
      "email": "vip@ejemplo.com",
      "firstName": "VIP",
      "lastName": "Customer"
    },
    "organizerId": 1,
    "items": [
      {
        "occurrenceId": 1,
        "eventVenueAreaId": 1,
        "eventVenueSeatId": 15,
        "ticketTypeId": 2,
        "unitPriceCents": 10000,
        "quantity": 1
      }
    ],
    "externalReference": "VIP-001"
  }'
```

### 3. Obtener Orden

```bash
curl -X GET http://localhost:8080/api/orders/1
```

### 4. Cancelar Orden

```bash
curl -X POST http://localhost:8080/api/orders/1/cancel \
  -H "Content-Type: application/json" \
  -d '{
    "reason": "Cambio de planes"
  }'
```

## ⚠️ Códigos de Error

| Código | Descripción |
|--------|-------------|
| 400 | Datos de entrada inválidos |
| 404 | Orden no encontrada |
| 409 | Conflicto (ej: asiento ya reservado) |
| 402 | Error de procesamiento de pago |
| 500 | Error interno del servidor |

### Ejemplo de Error de Validación

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Invalid request data",
  "details": {
    "customer.email": "Invalid email format",
    "items[0].quantity": "Quantity must be positive"
  },
  "path": "uri=/api/orders",
  "timestamp": "2024-10-25T15:30:00"
}
```

### Ejemplo de Error de Asiento Reservado

```json
{
  "code": "SEAT_ALREADY_RESERVED",
  "message": "Seat is already reserved: 15",
  "details": {
    "seatId": "15"
  },
  "path": "uri=/api/orders",
  "timestamp": "2024-10-25T15:30:00"
}
```

## 🔄 Flujo de Procesamiento

1. **Validación**: Se validan los datos de entrada
2. **Customer**: Se obtiene o crea el customer
3. **Orden**: Se crea la orden en estado `PENDING`
4. **Items**: Se crean los items de la orden
5. **Cálculo**: Se calcula el total de la orden
6. **Pago**: Se llama al payment-service para procesar el pago
7. **Actualización**: Se actualiza el estado según el resultado del pago
8. **Respuesta**: Se retorna la orden creada

## 🔧 Configuración

### Variables de Entorno

- `PAYMENT_SERVICE_URL`: URL del payment-service (default: http://payment-service:8080)
- `DB_HOST`: Host de la base de datos
- `DB_PORT`: Puerto de la base de datos
- `POSTGRES_DB`: Nombre de la base de datos
- `POSTGRES_USER`: Usuario de la base de datos
- `POSTGRES_PASSWORD`: Contraseña de la base de datos

### Swagger UI

La documentación interactiva está disponible en:
- Desarrollo: http://localhost:8080/swagger-ui.html
- Producción: http://order-service:8080/swagger-ui.html

## 🎯 Casos de Uso Soportados

### ✅ Implementados

- ✅ Registro de compra con pago automático
- ✅ Soporte para asientos específicos y admisión general
- ✅ Validación de asientos duplicados
- ✅ Gestión de customers (crear/actualizar)
- ✅ Trazabilidad completa con historial de estados
- ✅ Manejo de errores estructurado
- ✅ Integración con payment-service
- ✅ Cancelación de órdenes

### 🔄 Para Futuras Iteraciones

- 🔄 Reembolsos automáticos
- 🔄 Webhooks para notificaciones
- 🔄 Reservas temporales con expiración
- 🔄 Descuentos y promociones
- 🔄 Múltiples métodos de pago
- 🔄 Integración con notification-service
