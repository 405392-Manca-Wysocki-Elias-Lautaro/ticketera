# 🎫 Event Service - API Examples

Ejemplos de cURL para probar los endpoints del servicio de eventos en Postman o terminal.

## 📋 Tabla de Contenidos

1. [Configuración Inicial](#configuración-inicial)
2. [POST - Crear Evento Completo](#1-post---crear-evento-completo)
3. [GET - Obtener Todos los Eventos](#2-get---obtener-todos-los-eventos)
4. [GET - Obtener Evento por ID](#3-get---obtener-evento-por-id)
5. [GET - Obtener Eventos de Mi Organización (OWNER)](#4-get---obtener-eventos-de-mi-organización-owner)
6. [GET - Obtener Todos los Eventos (STAFF)](#5-get---obtener-todos-los-eventos-staff)
7. [PUT - Actualizar Evento](#6-put---actualizar-evento)
8. [DELETE - Eliminar Evento](#7-delete---eliminar-evento)

---

## Configuración Inicial

### Variables de Entorno (Postman)

```
BASE_URL=http://localhost:8080
JWT_TOKEN=tu_token_jwt_aqui
```

### Headers Comunes

```
Content-Type: application/json
Authorization: Bearer {{JWT_TOKEN}}
```

---

## 1. POST - Crear Evento Completo

Crea un evento completo con áreas, asientos y precios en una sola petición.

### cURL

```bash
curl -X POST http://localhost:8080/events \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "organizerId": "11111111-1111-1111-1111-111111111111",
    "title": "Festival de Rock 2025",
    "slug": "festival-rock-2025",
    "description": "El festival de rock más grande del año con las mejores bandas nacionales e internacionales",
    "categoryId": "bbbbbbbb-1111-1111-1111-bbbbbbbbbbbb",
    "coverUrl": "https://example.com/images/festival-rock-2025.jpg",
    "status": "published",
    "venueName": "Estadio Luna Park",
    "venueDescription": "Estadio emblemático de Buenos Aires con excelente acústica",
    "addressLine": "Av. Corrientes 465",
    "city": "Buenos Aires",
    "state": "Buenos Aires",
    "country": "Argentina",
    "lat": -34.6021,
    "lng": -58.3695,
    "startsAt": "2025-12-15T20:00:00",
    "endsAt": "2025-12-16T02:00:00",
    "areas": [
      {
        "name": "Campo VIP",
        "isGeneralAdmission": true,
        "capacity": 2000,
        "position": 1,
        "priceCents": 15000,
        "currency": "ARS",
        "seats": []
      },
      {
        "name": "Platea Alta",
        "isGeneralAdmission": false,
        "capacity": 500,
        "position": 2,
        "priceCents": 8000,
        "currency": "ARS",
        "seats": [
          {
            "seatNumber": 1,
            "rowNumber": 1,
            "label": "A-1"
          },
          {
            "seatNumber": 2,
            "rowNumber": 1,
            "label": "A-2"
          },
          {
            "seatNumber": 3,
            "rowNumber": 1,
            "label": "A-3"
          },
          {
            "seatNumber": 1,
            "rowNumber": 2,
            "label": "B-1"
          },
          {
            "seatNumber": 2,
            "rowNumber": 2,
            "label": "B-2"
          }
        ]
      },
      {
        "name": "Platea Baja",
        "isGeneralAdmission": false,
        "capacity": 300,
        "position": 3,
        "priceCents": 12000,
        "currency": "ARS",
        "seats": [
          {
            "seatNumber": 1,
            "rowNumber": 1,
            "label": "PA-1"
          },
          {
            "seatNumber": 2,
            "rowNumber": 1,
            "label": "PA-2"
          }
        ]
      }
    ]
  }'
```

### Response Esperado (201 Created)

```json
{
  "success": true,
  "message": "Evento completo creado exitosamente",
  "data": {
    "id": "cccccccc-3333-3333-3333-cccccccccccc",
    "organizerId": "11111111-1111-1111-1111-111111111111",
    "title": "Festival de Rock 2025",
    "slug": "festival-rock-2025",
    "description": "El festival de rock más grande del año...",
    "categoryId": "bbbbbbbb-1111-1111-1111-bbbbbbbbbbbb",
    "coverUrl": "https://example.com/images/festival-rock-2025.jpg",
    "status": "published",
    "venueName": "Estadio Luna Park",
    "venueDescription": "Estadio emblemático de Buenos Aires...",
    "addressLine": "Av. Corrientes 465",
    "city": "Buenos Aires",
    "state": "Buenos Aires",
    "country": "Argentina",
    "lat": -34.6021,
    "lng": -58.3695,
    "startsAt": "2025-12-15T20:00:00",
    "endsAt": "2025-12-16T02:00:00",
    "createdAt": "2025-11-12T22:30:00",
    "active": true
  },
  "timestamp": "2025-11-12T22:30:00Z"
}
```

---

## 2. GET - Obtener Todos los Eventos

Obtiene una lista de todos los eventos con información resumida (categoría, precios, disponibilidad).

### cURL

```bash
curl -X GET http://localhost:8080/events \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Response Esperado (200 OK)

```json
{
  "success": true,
  "message": "Eventos obtenidos exitosamente",
  "data": [
    {
      "id": "cccccccc-1111-1111-1111-cccccccccccc",
      "organizerId": "11111111-1111-1111-1111-111111111111",
      "title": "Show de Fito Páez en el Monumento a la Bandera",
      "slug": "fito-paez-monumento-rosario",
      "description": "Concierto especial de Fito Páez celebrando los 30 años...",
      "coverUrl": "https://example.com/images/fito-paez.jpg",
      "status": "published",
      "categoryId": "bbbbbbbb-1111-1111-1111-bbbbbbbbbbbb",
      "categoryName": "Conciertos",
      "venueName": "Estadio Monumental",
      "addressLine": "Av. Monseñor José Félix Uriburu 1234",
      "city": "Buenos Aires",
      "state": "Buenos Aires",
      "country": "Argentina",
      "lat": -34.603684,
      "lng": -58.381559,
      "startsAt": "2025-11-15T20:00:00",
      "endsAt": "2025-11-15T23:00:00",
      "minPriceCents": 5000,
      "currency": "ARS",
      "totalAvailableTickets": 5150,
      "totalCapacity": 5150
    },
    {
      "id": "cccccccc-2222-2222-2222-cccccccccccc",
      "organizerId": "11111111-1111-1111-1111-111111111111",
      "title": "Partido Newell's vs Rosario Central",
      "slug": "newells-vs-central",
      "description": "Clásico rosarino en el Estadio Marcelo Bielsa...",
      "coverUrl": "https://example.com/images/newells-central.jpg",
      "status": "published",
      "categoryId": "bbbbbbbb-2222-2222-2222-bbbbbbbbbbbb",
      "categoryName": "Deportes",
      "venueName": "Estadio Mario Alberto Kempes",
      "addressLine": "Avenida Ramón Cárcano",
      "city": "Córdoba",
      "state": "Córdoba",
      "country": "Argentina",
      "lat": -31.4427,
      "lng": -64.2356,
      "startsAt": "2025-10-25T18:00:00",
      "endsAt": "2025-10-25T20:00:00",
      "minPriceCents": 15000,
      "currency": "ARS",
      "totalAvailableTickets": 150,
      "totalCapacity": 150
    }
  ],
  "timestamp": "2025-11-12T22:30:00Z"
}
```

---

## 3. GET - Obtener Evento por ID

Obtiene el detalle completo de un evento específico, incluyendo áreas, precios y disponibilidad.

### cURL

```bash
curl -X GET http://localhost:8080/events/cccccccc-1111-1111-1111-cccccccccccc \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Response Esperado (200 OK)

```json
{
  "success": true,
  "message": "Detalle del evento obtenido exitosamente",
  "data": {
    "id": "cccccccc-1111-1111-1111-cccccccccccc",
    "organizerId": "11111111-1111-1111-1111-111111111111",
    "title": "Show de Fito Páez en el Monumento a la Bandera",
    "slug": "fito-paez-monumento-rosario",
    "description": "Concierto especial de Fito Páez celebrando los 30 años de 'El amor después del amor'.",
    "coverUrl": "https://example.com/images/fito-paez.jpg",
    "status": "published",
    "createdAt": "2025-11-01T10:00:00",
    "categoryId": "bbbbbbbb-1111-1111-1111-bbbbbbbbbbbb",
    "categoryName": "Conciertos",
    "categoryDescription": "Eventos musicales en vivo de diferentes géneros y artistas.",
    "venueName": "Estadio Monumental",
    "venueDescription": "Estadio Monumental de Núñez",
    "addressLine": "Av. Monseñor José Félix Uriburu 1234",
    "city": "Buenos Aires",
    "state": "Buenos Aires",
    "country": "Argentina",
    "lat": -34.603684,
    "lng": -58.381559,
    "startsAt": "2025-11-15T20:00:00",
    "endsAt": "2025-11-15T23:00:00",
    "areas": [
      {
        "id": "11111111-1111-1111-1111-111111111111",
        "name": "Campo",
        "isGeneralAdmission": true,
        "capacity": 5000,
        "position": 1,
        "priceCents": 5000,
        "currency": "ARS",
        "availableTickets": 5000,
        "totalSeats": 5000
      },
      {
        "id": "22222222-2222-2222-2222-222222222222",
        "name": "Zona VIP",
        "isGeneralAdmission": false,
        "capacity": 150,
        "position": 2,
        "priceCents": 15000,
        "currency": "ARS",
        "availableTickets": 150,
        "totalSeats": 150
      }
    ],
    "totalAvailableTickets": 5150,
    "totalCapacity": 5150,
    "minPriceCents": 5000,
    "maxPriceCents": 15000,
    "currency": "ARS"
  },
  "timestamp": "2025-11-12T22:30:00Z"
}
```

### Response Error (404 Not Found)

```json
{
  "success": false,
  "message": "Evento no encontrado con ID: cccccccc-9999-9999-9999-cccccccccccc",
  "timestamp": "2025-11-12T22:30:00Z"
}
```

---

## 4. GET - Obtener Eventos de Mi Organización (OWNER)

**🔐 Requiere rol: OWNER**

Obtiene todos los eventos de la organización del usuario autenticado. El `organizerId` se extrae automáticamente del JWT.

### cURL

```bash
curl -X GET http://localhost:8080/events/my-organization \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_WITH_OWNER_ROLE"
```

### JWT Requerido

El token JWT debe contener:
```json
{
  "sub": "user-uuid",
  "email": "owner@example.com",
  "role": "OWNER",
  "organizerId": "11111111-1111-1111-1111-111111111111"
}
```

### Response Esperado (200 OK)

```json
{
  "success": true,
  "message": "Eventos de la organización obtenidos exitosamente",
  "data": [
    {
      "id": "cccccccc-1111-1111-1111-cccccccccccc",
      "organizerId": "11111111-1111-1111-1111-111111111111",
      "title": "Show de Fito Páez",
      "slug": "fito-paez-monumento-rosario",
      "description": "Concierto especial...",
      "coverUrl": "https://example.com/images/fito-paez.jpg",
      "status": "published",
      "categoryId": "bbbbbbbb-1111-1111-1111-bbbbbbbbbbbb",
      "categoryName": "Conciertos",
      "venueName": "Estadio Monumental",
      "addressLine": "Av. Monseñor José Félix Uriburu 1234",
      "city": "Buenos Aires",
      "state": "Buenos Aires",
      "country": "Argentina",
      "lat": -34.603684,
      "lng": -58.381559,
      "startsAt": "2025-11-15T20:00:00",
      "endsAt": "2025-11-15T23:00:00",
      "minPriceCents": 5000,
      "currency": "ARS",
      "totalAvailableTickets": 5150,
      "totalCapacity": 5150
    }
  ],
  "timestamp": "2025-11-12T22:30:00Z"
}
```

### Response Error (403 Forbidden)

Si el usuario no tiene rol OWNER:

```json
{
  "success": false,
  "message": "Solo los OWNER pueden acceder a esta funcionalidad",
  "timestamp": "2025-11-12T22:30:00Z"
}
```

### Response Error (401 Unauthorized)

Si el JWT no contiene el claim `organizerId`:

```json
{
  "success": false,
  "message": "JWT claim not found: organizerId",
  "timestamp": "2025-11-12T22:30:00Z"
}
```

---

## 5. GET - Obtener Todos los Eventos (STAFF)

**🔐 Requiere rol: STAFF o OWNER**

Permite al personal administrativo ver todos los eventos del sistema.

### cURL

```bash
curl -X GET http://localhost:8080/events/staff \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_WITH_STAFF_ROLE"
```

### JWT Requerido

El token JWT debe contener:
```json
{
  "sub": "user-uuid",
  "email": "staff@example.com",
  "role": "STAFF"
}
```

O:

```json
{
  "sub": "user-uuid",
  "email": "owner@example.com",
  "role": "OWNER",
  "organizerId": "11111111-1111-1111-1111-111111111111"
}
```

### Response Esperado (200 OK)

```json
{
  "success": true,
  "message": "Eventos obtenidos exitosamente",
  "data": [
    {
      "id": "cccccccc-1111-1111-1111-cccccccccccc",
      "organizerId": "11111111-1111-1111-1111-111111111111",
      "title": "Show de Fito Páez",
      "categoryName": "Conciertos",
      "venueName": "Estadio Monumental",
      "startsAt": "2025-11-15T20:00:00",
      "minPriceCents": 5000,
      "totalAvailableTickets": 5150
    },
    {
      "id": "cccccccc-2222-2222-2222-cccccccccccc",
      "organizerId": "22222222-2222-2222-2222-222222222222",
      "title": "Festival Lollapalooza",
      "categoryName": "Conciertos",
      "venueName": "Hipódromo de San Isidro",
      "startsAt": "2025-12-10T14:00:00",
      "minPriceCents": 25000,
      "totalAvailableTickets": 80000
    }
  ],
  "timestamp": "2025-11-12T22:30:00Z"
}
```

### Response Error (403 Forbidden)

Si el usuario no tiene rol STAFF ni OWNER:

```json
{
  "success": false,
  "message": "Solo el personal autorizado puede acceder a esta funcionalidad",
  "timestamp": "2025-11-12T22:30:00Z"
}
```

---

## 6. PUT - Actualizar Evento

Actualiza los datos básicos de un evento existente.

### cURL

```bash
curl -X PUT http://localhost:8080/events/cccccccc-1111-1111-1111-cccccccccccc \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "organizerId": "11111111-1111-1111-1111-111111111111",
    "title": "Show de Fito Páez - ACTUALIZADO",
    "slug": "fito-paez-monumento-rosario-2025",
    "description": "Concierto especial actualizado con nueva información",
    "categoryId": "bbbbbbbb-1111-1111-1111-bbbbbbbbbbbb",
    "coverUrl": "https://example.com/images/fito-paez-new.jpg",
    "status": "published",
    "venueName": "Estadio Monumental",
    "venueDescription": "Estadio Monumental de Núñez - Actualizado",
    "addressLine": "Av. Monseñor José Félix Uriburu 1234",
    "city": "Buenos Aires",
    "state": "Buenos Aires",
    "country": "Argentina",
    "lat": -34.603684,
    "lng": -58.381559,
    "startsAt": "2025-11-15T21:00:00",
    "endsAt": "2025-11-16T00:00:00"
  }'
```

### Response Esperado (200 OK)

```json
{
  "success": true,
  "message": "Event updated successfully",
  "data": {
    "id": "cccccccc-1111-1111-1111-cccccccccccc",
    "organizerId": "11111111-1111-1111-1111-111111111111",
    "title": "Show de Fito Páez - ACTUALIZADO",
    "slug": "fito-paez-monumento-rosario-2025",
    "status": "published"
  },
  "timestamp": "2025-11-12T22:30:00Z"
}
```

---

## 7. DELETE - Eliminar Evento

Realiza un soft delete del evento (marca como inactivo).

### cURL

```bash
curl -X DELETE http://localhost:8080/events/cccccccc-1111-1111-1111-cccccccccccc \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Response Esperado (200 OK)

```json
{
  "success": true,
  "message": "Event deleted successfully",
  "timestamp": "2025-11-12T22:30:00Z"
}
```

### Response Error (404 Not Found)

```json
{
  "success": false,
  "message": "Event not found with id cccccccc-9999-9999-9999-cccccccccccc",
  "timestamp": "2025-11-12T22:30:00Z"
}
```

---

## 🔑 Notas sobre Autenticación

### Estructura del JWT

El JWT debe contener los siguientes claims según el rol:

**Para OWNER:**
```json
{
  "sub": "user-uuid",
  "email": "owner@example.com",
  "role": "OWNER",
  "organizerId": "org-uuid"
}
```

**Para STAFF:**
```json
{
  "sub": "user-uuid",
  "email": "staff@example.com",
  "role": "STAFF"
}
```

### Endpoints por Rol

| Endpoint | Público | OWNER | STAFF |
|----------|---------|-------|-------|
| `POST /events` | ✅ | ✅ | ✅ |
| `GET /events` | ✅ | ✅ | ✅ |
| `GET /events/{id}` | ✅ | ✅ | ✅ |
| `GET /events/my-organization` | ❌ | ✅ | ❌ |
| `GET /events/staff` | ❌ | ✅ | ✅ |
| `PUT /events/{id}` | ✅ | ✅ | ✅ |
| `DELETE /events/{id}` | ✅ | ✅ | ✅ |

---

## 🧪 Testing en Postman

### Importar Colección

1. Crea una nueva colección llamada "Event Service"
2. Crea las siguientes variables:
   - `BASE_URL`: `http://localhost:8080`
   - `JWT_TOKEN`: Tu token JWT
3. Copia y pega cada cURL en Postman usando "Import" → "Raw text"

### Variables de Entorno

```json
{
  "BASE_URL": "http://localhost:8080",
  "JWT_TOKEN": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "EVENT_ID": "cccccccc-1111-1111-1111-cccccccccccc",
  "ORGANIZER_ID": "11111111-1111-1111-1111-111111111111",
  "CATEGORY_ID": "bbbbbbbb-1111-1111-1111-bbbbbbbbbbbb"
}
```

---

## 📝 Códigos de Estado HTTP

| Código | Significado | Cuándo ocurre |
|--------|-------------|---------------|
| 200 | OK | Operación exitosa (GET, PUT, DELETE) |
| 201 | Created | Evento creado exitosamente (POST) |
| 400 | Bad Request | Validación fallida en el body |
| 401 | Unauthorized | JWT inválido o no presente |
| 403 | Forbidden | Sin permisos para la operación |
| 404 | Not Found | Evento no encontrado |
| 500 | Internal Server Error | Error del servidor |

---

## 🚀 Quick Start

### 1. Crear un evento de prueba

```bash
curl -X POST http://localhost:8080/events \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT" \
  -d @evento-ejemplo.json
```

### 2. Ver todos los eventos

```bash
curl http://localhost:8080/events \
  -H "Authorization: Bearer YOUR_JWT"
```

### 3. Ver detalle de un evento

```bash
curl http://localhost:8080/events/EVENT_ID \
  -H "Authorization: Bearer YOUR_JWT"
```

### 4. Ver mis eventos (OWNER)

```bash
curl http://localhost:8080/events/my-organization \
  -H "Authorization: Bearer YOUR_JWT_OWNER"
```

---

## 📞 Contacto y Soporte

Para más información sobre la API, consulta la documentación Swagger en:

```
http://localhost:8080/swagger-ui.html
```

---

**Última actualización:** 2025-11-12

