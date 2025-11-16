import api from "@/lib/api";
import type { TicketResponse } from "@/types/Ticket";
import type { ApiResponse } from "@/types/Response/ApiResponse";

export const ticketService = {
  /**
   * Obtiene todos los tickets de un usuario por su ID
   */
  async getUserTickets(userId: string): Promise<TicketResponse[]> {
    const response = await api.get<ApiResponse<TicketResponse[]>>(`/tickets/user/${userId}`);
    return response.data.data;
  },

  /**
   * Obtiene un ticket específico por su ID
   */
  async getTicketById(ticketId: string): Promise<TicketResponse> {
    const response = await api.get<ApiResponse<TicketResponse>>(`/tickets/${ticketId}`);
    return response.data.data;
  },

  /**
   * Valida un ticket por su código QR o código manual
   */
  async validateTicket(type: 'QR' | 'CODE', value: string): Promise<TicketResponse> {
    const response = await api.post<ApiResponse<TicketResponse>>('/tickets/validate', {
      type,
      value
    });
    return response.data.data;
  },

  /**
   * Realiza el check-in de un ticket
   */
  async checkInTicket(ticketId: string): Promise<TicketResponse> {
    const response = await api.post<ApiResponse<TicketResponse>>(`/tickets/${ticketId}/check-in`);
    return response.data.data;
  }
};

