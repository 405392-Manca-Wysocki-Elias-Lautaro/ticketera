export interface CreateOrderRequest {
  customer: {
    email: string;
    firstName: string;
    lastName: string;
    phone: string;
    userId?: string; // UUID from auth-service
  };
  organizerId: number;
  items: OrderItem[];
  currency: string;
  notes?: string;
  externalReference?: string;
  expiresAt?: string;
  paymentDescription?: string;
}

export interface OrderItem {
  // ID del evento
  eventId: number;
  
  // ID del área del evento (ej: Campo, Platea, etc.)
  venueAreaId: number;
  
  // ID del asiento específico (opcional, solo para asientos numerados)
  venueSeatId?: number;
  
  // Tipo de ticket (ej: adulto, niño, VIP, etc.)
  ticketTypeId: number;
  
  // Precio unitario en centavos
  unitPriceCents: number;
  
  // Cantidad de tickets (debe ser 1 si es asiento numerado)
  quantity: number;
}

export interface OrderResponse {
  id: number;
  customer: {
    id: number;
    email: string;
    firstName: string;
    lastName: string;
    phone: string;
    userId?: string; // UUID from auth-service
  };
  organizerId: number;
  status: string;
  totalCents: number;
  currency: string;
  expiresAt?: string;
  paymentMethod?: string;
  notes?: string;
  externalReference?: string;
  createdAt: string;
  updatedAt: string;
  paidAt?: string;
  items: OrderItemResponse[];
  paymentUrl?: string; // URL de redirección a Mercado Pago
  preferenceId?: string; // ID de la preferencia de Mercado Pago
}

export interface OrderItemResponse {
  id: number;
  
  // ID del evento
  eventId: number;
  
  // ID del área del evento
  venueAreaId: number;
  
  // ID del asiento específico (si aplica)
  venueSeatId?: number;
  
  // Tipo de ticket
  ticketTypeId: number;
  
  // Precio unitario en centavos
  unitPriceCents: number;
  
  // Cantidad
  quantity: number;
  
  // Precio total en centavos (unitPrice * quantity)
  totalPriceCents: number;
}

