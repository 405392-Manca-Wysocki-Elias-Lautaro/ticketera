import api from "@/lib/api";
import type { CreateOrderRequest, OrderResponse } from "@/types/Order";
import type { ApiResponse } from "@/types/Response/ApiResponse";

export const orderService = {
  /**
   * Crea una nueva orden y obtiene la URL de pago de Mercado Pago
   */
  async createOrder(orderData: CreateOrderRequest): Promise<OrderResponse> {
    const response = await api.post<ApiResponse<OrderResponse>>("/orders/create", orderData);
    return response.data.data;
  },

  /**
   * Obtiene una orden por su ID
   */
  async getOrder(orderId: string): Promise<OrderResponse> {
    const response = await api.get<ApiResponse<OrderResponse>>(`/orders/${orderId}`);
    return response.data.data;
  },

  /**
   * Obtiene las órdenes de un cliente
   */
  async getCustomerOrders(customerId: string): Promise<OrderResponse[]> {
    const response = await api.get<ApiResponse<OrderResponse[]>>(`/orders/customer/${customerId}`);
    return response.data.data;
  },

  /**
   * Consulta el estado del pago de una orden (usado para polling).
   * Usa _silent para no mostrar toasts de error globales.
   */
  async getPaymentStatus(orderId: string): Promise<{ orderId: string; status: string }> {
    const response = await api.get(`/payments/orders/${orderId}/status`, {
      _silent: true,
    } as any);
    return response.data?.data || response.data;
  },
};

