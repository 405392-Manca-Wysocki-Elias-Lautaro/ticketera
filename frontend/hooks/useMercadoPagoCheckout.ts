import { useEffect, useRef, useState } from "react";
import { MERCADOPAGO_CONFIG, validateMercadoPagoConfig, isTestMode } from "@/config/mercadopago";

declare global {
  interface Window {
    MercadoPago: any;
  }
}

interface UseMercadoPagoCheckoutProps {
  preferenceId?: string;
  onReady?: () => void;
  onError?: (error: Error) => void;
}

export const useMercadoPagoCheckout = ({
  preferenceId,
  onReady,
  onError,
}: UseMercadoPagoCheckoutProps) => {
  const [isSDKReady, setIsSDKReady] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const mpInstanceRef = useRef<any>(null);
  const bricksBuilderRef = useRef<any>(null);

  // Inicializar el SDK de Mercado Pago
  useEffect(() => {
    const checkSDK = () => {
      if (typeof window !== "undefined" && window.MercadoPago) {
        // Validar configuración
        if (!validateMercadoPagoConfig()) {
          const error = new Error("Mercado Pago Public Key no configurada correctamente");
          console.error(error);
          onError?.(error);
          return;
        }

        try {
          console.log(`🔧 Inicializando Mercado Pago en modo: ${isTestMode() ? 'TEST' : 'PRODUCCIÓN'}`);
          
          mpInstanceRef.current = new window.MercadoPago(MERCADOPAGO_CONFIG.publicKey, {
            locale: MERCADOPAGO_CONFIG.locale,
          });
          
          setIsSDKReady(true);
          console.log('✅ SDK de Mercado Pago inicializado correctamente');
        } catch (error) {
          console.error("❌ Error inicializando Mercado Pago:", error);
          onError?.(error as Error);
        }
      } else {
        // Reintentar después de un pequeño delay
        setTimeout(checkSDK, 100);
      }
    };

    checkSDK();
  }, [onError]);

  // Renderizar el botón de pago cuando tengamos el preferenceId
  const renderPaymentButton = async (containerId: string) => {
    if (!isSDKReady || !mpInstanceRef.current || !preferenceId) {
      console.warn("SDK no está listo o falta el preferenceId");
      return;
    }

    setIsLoading(true);

    try {
      // Limpiar cualquier instancia anterior
      const container = document.getElementById(containerId);
      if (container) {
        container.innerHTML = "";
      }

      // Crear el Bricks Builder
      bricksBuilderRef.current = mpInstanceRef.current.bricks();

      // Renderizar el Wallet Brick (botón de pago)
      await bricksBuilderRef.current.create("wallet", containerId, {
        initialization: {
          preferenceId: preferenceId,
        },
        customization: {
          texts: {
            valueProp: "smart_option",
          },
        },
      });

      setIsLoading(false);
      onReady?.();
    } catch (error) {
      console.error("Error renderizando botón de pago:", error);
      setIsLoading(false);
      onError?.(error as Error);
    }
  };

  // Limpiar al desmontar
  useEffect(() => {
    return () => {
      // Limpiar instancias si es necesario
      bricksBuilderRef.current = null;
    };
  }, []);

  return {
    isSDKReady,
    isLoading,
    renderPaymentButton,
  };
};

