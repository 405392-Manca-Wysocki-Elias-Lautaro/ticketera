import api from '@/lib/api'
import { ApiResponse } from '@/types/Response/ApiResponse'
import { Ticket } from '@/types/Ticket'

const BASE_URL = "/tickets"

export const ticketService = {
    getByUserId: () => api.get<ApiResponse<Ticket>>(`${BASE_URL}/user`),
}