import api from "@/lib/api";
import type { Ticket } from "@/types/Ticket";
import type { ApiResponse } from "@/types/Response/ApiResponse";
import { ValidateTicket } from '@/types/Request/ValidateTicket';

const BASE_URL = "/tickets"

export const ticketService = {
  /**
   * Obtiene todos los tickets de un usuario por su ID
   */
  async getUserTickets(userId: string): Promise<Ticket[]> {
    const response = await api.get<ApiResponse<Ticket[]>>(`/tickets/user/${userId}`);
    return response.data.data;
  },

  /**
   * Obtiene un ticket específico por su ID
   */
  async getTicketById(ticketId: string): Promise<Ticket> {
    const response = await api.get<ApiResponse<Ticket>>(`/tickets/${ticketId}`);
    return response.data.data;
  },

  /**
   * Valida un ticket por su código QR o código manual
   */
  async validateTicket(type: 'QR' | 'CODE', value: string): Promise<Ticket> {
    const response = await api.post<ApiResponse<Ticket>>('/tickets/validate', {
      type,
      value
    });
    return response.data.data;
  },

  /**
   * Realiza el check-in de un ticket
   */
  async checkInTicket(ticketId: string): Promise<Ticket> {
    const response = await api.post<ApiResponse<Ticket>>(`/tickets/${ticketId}/check-in`);
    return response.data.data;
  },


    getByUserId: () => api.get<ApiResponse<Ticket>>(`${BASE_URL}/user`),
    validate: (data: ValidateTicket) => api.post<ApiResponse<Ticket>>(`${BASE_URL}/validate`, data),

};

