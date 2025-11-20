import { ticketService } from '@/services/ticketService'
import { Ticket } from '@/types/Ticket'
import { useQuery } from "@tanstack/react-query"


export function useTicketsByUser() {
    return useQuery<Ticket[]>({
        queryKey: ["tickets-user"],
        queryFn: async () => {
            const { data } = await ticketService.getByUserId();
            return data.data;
        },
        placeholderData: (prev: any) => prev,
        staleTime: 1000 * 60 * 120,
        refetchOnWindowFocus: false,
        refetchOnReconnect: false,
        refetchOnMount: false,
        refetchInterval: false,

    })
}