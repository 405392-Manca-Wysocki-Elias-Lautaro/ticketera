/**
 * Configuración de Mercado Pago
 * 
 * La Public Key se puede configurar de varias formas:
 * 1. Variable de entorno: MERCADOPAGO_PUBLIC_KEY (configurada en next.config.mjs)
 * 2. Fallback en next.config.mjs
 */

export const MERCADOPAGO_CONFIG = {
  // Public Key - Segura de exponer en el frontend
  // @ts-ignore - env está configurado en next.config.mjs
  publicKey: process.env.MERCADOPAGO_PUBLIC_KEY || 'APP_USR-5dab3fb3-e699-43b4-996f-a65f976a354a',
  
  // Configuración regional
  locale: 'es-AR' as const,
  
  // URLs base según ambiente
  apiUrl: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080',
} as const;

/**
 * Valida si la configuración de Mercado Pago está completa
 */
export function validateMercadoPagoConfig(): boolean {
  if (!MERCADOPAGO_CONFIG.publicKey) {
    console.error('❌ MERCADOPAGO_PUBLIC_KEY no está configurada');
    return false;
  }
  
  if (!MERCADOPAGO_CONFIG.publicKey.startsWith('APP_USR-')) {
    console.warn('⚠️ La Public Key no tiene el formato esperado (APP_USR-...)');
    return false;
  }
  
  return true;
}

/**
 * Detecta si estamos usando credenciales de test o producción
 */
export function isTestMode(): boolean {
  // Las credenciales de test de Mercado Pago suelen tener un formato específico
  // o puedes usar una variable de entorno adicional para controlarlo
  return process.env.NODE_ENV !== 'production' || 
         process.env.NEXT_PUBLIC_MP_TEST_MODE === 'true';
}

