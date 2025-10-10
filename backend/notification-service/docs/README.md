# 📬 Notification Service

Microservicio encargado de **enviar notificaciones** a los usuarios a través de distintos canales (Email, SMS, WhatsApp, Telegram).  
Forma parte de la arquitectura distribuida de **Ticketera** y se comunica con otros servicios (por ejemplo, `auth-service`, `order-service`, etc.) mediante REST o mensajería asíncrona.

---

## 🚀 Objetivo

Proveer una interfaz unificada para que cualquier microservicio del ecosistema pueda solicitar el envío de mensajes sin preocuparse por la lógica específica de cada canal (plantillas, integración con proveedores, etc.).

---

## 🧩 Entidades principales

### **NotificationChannel**

Enum que representa los canales de comunicación disponibles:

| Canal | Descripción |
|--------|-------------|
| `EMAIL` | Envío de correos electrónicos |
| `SMS` | Envío de mensajes de texto |
| `WHATSAPP` | Envío de mensajes por WhatsApp |
| `TELEGRAM` | Envío de mensajes por Telegram |

📁 `com.notification.app.entity.NotificationChannel`

```java
public enum NotificationChannel {
    EMAIL,
    SMS,
    WHATSAPP,
    TELEGRAM
}
```

---

### **NotificationType**

Enum que define los distintos tipos de notificación soportados y los canales válidos para cada uno.  
Esto evita que se envíen notificaciones por un canal no permitido (por ejemplo, enviar una verificación de email por WhatsApp).

📁 `com.notification.app.entity.NotificationType`

```java
public enum NotificationType {
    // 🔐 Autenticación
    EMAIL_VERIFICATION(EnumSet.of(NotificationChannel.EMAIL)),
    PASSWORD_RESET(EnumSet.of(NotificationChannel.EMAIL)),
    USER_WELCOME(EnumSet.of(NotificationChannel.EMAIL));

    private final Set<NotificationChannel> validChannels;

    NotificationType(Set<NotificationChannel> validChannels) {
        this.validChannels = validChannels;
    }

    public Set<NotificationChannel> getValidChannels() {
        return validChannels;
    }

    public boolean supports(NotificationChannel channel) {
        return validChannels.contains(channel);
    }
}
```

---

## 📤 Endpoint principal

### `POST /api/notifications`

Permite solicitar el envío de una notificación por un canal determinado.  
El servicio validará que el **tipo (`type`) y el canal (`channel`) sean compatibles** según la configuración de los enums.

#### 📩 Request body

```json
{
  "channel": "EMAIL",
  "type": "USER_WELCOME",
  "recipient": "user@example.com",
  "data": {
    "subject": "¡Bienvenido a Ticketera!",
    "template": "welcome-email.html",
    "userName": "Facundo",
    "email": "user@example.com"
  }
}
```

#### 📦 Campos

| Campo | Tipo | Obligatorio | Descripción |
|--------|------|--------------|--------------|
| `channel` | `string` | ✅ | Canal de comunicación (`EMAIL`, `SMS`, `WHATSAPP`, `TELEGRAM`) |
| `type` | `string` | ✅ | Tipo de notificación (`EMAIL_VERIFICATION`, `PASSWORD_RESET`, `USER_WELCOME`, etc.) |
| `recipient` | `string` | ✅ | Destinatario principal (correo electrónico, número de teléfono, etc.) |
| `data` | `object` | ✅ | Mapa de datos dinámicos utilizados en la plantilla del mensaje (asunto, variables, enlaces, etc.) |

---

## 🧠 Ejemplos de uso

### 👋 Bienvenida de usuario

```json
{
  "channel": "EMAIL",
  "type": "USER_WELCOME",
  "to": "user@example.com",
  "variables": {
    "subject": "¡Bienvenido a Ticketera!",
    "userName": "Facundo",
    "template": "welcome-email.html"
  }
}
```

### 🔑 Recuperación de contraseña

```json
{
  "channel": "EMAIL",
  "type": "PASSWORD_RESET",
  "to": "user@example.com",
  "variables": {
    "subject": "Restablecé tu contraseña",
    "resetLink": "https://auth.ticketera.com/reset?token=xyz789"
  }
}
```

### 📧 Verificación de email

```json
{
  "channel": "EMAIL",
  "type": "EMAIL_VERIFICATION",
  "to": "user@example.com",
  "variables": {
    "subject": "Verificá tu cuenta",
    "verificationLink": "https://auth.ticketera.com/verify?token=abc123"
  }
}
```

---

## ❌ Errores comunes

| Código | Error | Descripción |
|---------|--------|-------------|
| `400 Bad Request` | `InvalidNotificationType` | El tipo de notificación no existe o no es soportado |
| `400 Bad Request` | `InvalidNotificationChannel` | El canal elegido no está permitido para ese tipo |
| `422 Unprocessable Entity` | `MissingData` | Faltan datos requeridos en el campo `data` |
| `500 Internal Server Error` | `DeliveryFailed` | Falló el envío (problema en el proveedor externo) |

### Ejemplo de error

```json
{
  "status": 400,
  "error": "InvalidNotificationChannel",
  "message": "El tipo USER_WELCOME solo admite el canal EMAIL."
}
```

---

## ⚙️ Integración con otros servicios

Otros microservicios (como `auth-service`, `order-service`, etc.) pueden invocar este endpoint utilizando una abstracción común llamada `NotificationSender`.

Ejemplo desde `AuthServiceImpl` en el servicio de autenticación:

```java
notificationSender.send(
    NotificationDTO.builder()
        .channel("EMAIL")
        .type("USER_WELCOME")
        .recipient(saved.getEmail())
        .data(Map.of(
            "subject", "¡Bienvenido a Ticketera!",
            "userName", saved.getName(),
            "template", "welcome-email.html"
        ))
        .build()
);
```

---

## 🧱 Arquitectura y configuración

- **Lenguaje:** Java 17  
- **Framework:** Spring Boot 3.x  
- **Mensajería opcional:** RabbitMQ / REST  
- **Serialización:** JSON  
- **Plantillas:** Thymeleaf o Freemarker (para correos)  

