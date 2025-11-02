import { ApiErrorResponse } from '@/types/Response/ApiErrorResponse'
import { toast } from "sonner"

/**
 * Manejador global de errores provenientes de cualquier microservicio (Auth, Gateway, etc.)
 * Traduce los códigos técnicos del backend a mensajes claros para el usuario final.
 */
export function handleApiError(error: any) {
  const response: ApiErrorResponse = error?.response?.data ?? {}
  const code = response.data?.code
  const details = response.data?.details
  const message = response.data?.message
  const status = response.status ?? error?.response?.status

  let userMessage = "Ocurrió un error inesperado. Intenta nuevamente."

  switch (code) {
    // 🔐 AUTENTICACIÓN / TOKEN
    case "INVALID_CREDENTIALS":
      userMessage = "Email o contraseña incorrectos."
      break
    case "TOKEN_EXPIRED":
      userMessage = "Tu sesión ha expirado. Por favor, vuelve a iniciar sesión."
      break
    case "INVALID_TOKEN":
      userMessage = "Tu sesión no es válida. Inicia sesión nuevamente."
      break
    case "MALFORMED_TOKEN":
      userMessage = "Token de autenticación mal formado o corrupto."
      break
    case "MISSING_TOKEN":
      userMessage = "No se encontró el token de autenticación. Inicia sesión nuevamente."
      break
    case "BLACKLISTED_TOKEN":
      userMessage = "Este token ya no es válido. Inicia sesión otra vez."
      break
    case "TOKEN_REVOKED":
      userMessage = "El token fue revocado por seguridad. Inicia sesión nuevamente."
      break
    case "TOKEN_NOT_FOUND":
      userMessage = "No se encontró información de sesión válida."
      break

    // 🚪 GATEWAY / VALIDACIÓN JWT
    case "TOKEN_VALIDATION_FAILED":
      userMessage = "Error al validar tu sesión. Por favor, vuelve a iniciar sesión."
      break
    case "REDIS_CONNECTION_FAILED":
      userMessage = "Hubo un problema al validar tu sesión. Intenta nuevamente más tarde."
      break
    case "TOKEN_ALREADY_EXPIRED":
      userMessage = "El token de acceso ya expiró. Por favor, inicia sesión de nuevo."
      break
    case "TOKEN_INVALID_SIGNATURE":
      userMessage = "La firma del token no es válida. Se requiere volver a autenticarse."
      break
    case "JWT_EXPIRED":
      userMessage = "Tu sesión expiró. Vuelve a iniciar sesión."
      break

    // 🔒 AUTORIZACIÓN
    case "UNAUTHORIZED":
      userMessage = "No tienes autorización para realizar esta acción."
      break
    case "FORBIDDEN":
      userMessage = "No tienes permisos suficientes para acceder a este recurso."
      break

    // 👤 USUARIOS / CUENTAS
    case "USER_NOT_FOUND":
      userMessage = "No se encontró el usuario especificado."
      break
    case "USER_ALREADY_EXISTS":
      userMessage = "Ya existe un usuario con ese correo electrónico."
      break
    case "ACCOUNT_NOT_VERIFIED":
      userMessage = "Debes verificar tu cuenta antes de continuar."
      break
    case "EMAIL_ALREADY_EXISTS":
      userMessage = "El correo electrónico ya está registrado."
      break

    // 📬 TOKENS Y VERIFICACIÓN
    case "INVALID_OR_UNKNOWN_TOKEN":
      userMessage = "El enlace o token no es válido o ha expirado."
      break
    case "TOKEN_ALREADY_USED":
      userMessage = "El token ya fue utilizado previamente."
      break
    case "EMAIL_VERIFICATION_TOKEN_EXPIRED":
      userMessage = "El enlace de verificación ha expirado. Solicita uno nuevo."
      break

    // ⚙️ VALIDACIONES DE ENTRADA
    case "VALIDATION_FAILED":
      userMessage = "Revisa los datos ingresados: hay errores o campos incompletos."
      break
    case "MISSING_FIELDS":
      userMessage = "Faltan campos obligatorios en la solicitud."
      break
    case "INVALID_FORMAT":
      userMessage = "Algunos campos tienen un formato incorrecto."
      break
    case "WEAK_PASSWORD":
      userMessage = "La contraseña elegida es demasiado débil."
      break
    case "COMMON_PASSWORD":
      userMessage = "La contraseña es demasiado común o insegura."
      break

    // 🌍 RECURSOS Y ENTIDADES
    case "ENTITY_NOT_FOUND":
      if (details?.entity) {
        const entity = details.entity
          .replace(/_/g, " ")
          .toLowerCase()
          .replace(/\b\w/g, (l: any) => l.toUpperCase())
        const value = details.value ? ` (${details.value})` : ""
        userMessage = `No se encontró ${entity}${value}.`
      } else {
        userMessage = "El recurso solicitado no existe o fue eliminado."
      }
      break
    case "RESOURCE_NOT_FOUND":
      userMessage = "No se encontró el recurso solicitado."
      break
    case "BAD_REQUEST":
      userMessage = "La solicitud enviada no es válida."
      break
    case "CONFLICT":
      userMessage = "Los datos enviados entran en conflicto con registros existentes."
      break

    // ☁️ INFRAESTRUCTURA / SERVICIOS
    case "DATABASE_UNAVAILABLE":
      userMessage = "No se pudo conectar con la base de datos. Intenta nuevamente más tarde."
      break
    case "SERVICE_UNAVAILABLE":
      userMessage = "El servicio no está disponible en este momento."
      break
    case "INTEGRATION_ERROR":
      userMessage = "Ocurrió un error al comunicarse con un servicio externo."
      break
    case "STORAGE_ERROR":
      userMessage = "Error al acceder al sistema de archivos o almacenamiento."
      break
    case "NETWORK_ERROR":
      userMessage = "Hubo un problema de conexión. Verifica tu red e intenta otra vez."
      break

    // 🔧 GENERALES / FALLBACK
    case "INTERNAL_SERVER_ERROR":
      userMessage = "Ocurrió un error interno. Intenta nuevamente más tarde."
      break
    default:
      if (message) userMessage = message
      else if (status === 401)
        userMessage = "Tu sesión no es válida o ha expirado. Vuelve a iniciar sesión."
      else if (status === 404)
        userMessage = "No se encontró el recurso solicitado."
      else if (status === 500)
        userMessage = "Error interno del servidor. Intenta nuevamente."
  }

  toast.error(userMessage)
  console.error("API Error:", { code, details, status, message })
}
