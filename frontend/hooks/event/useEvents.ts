import { eventService } from '@/services/eventService'
import { Event } from '@/types/Event'
import { useQuery } from "@tanstack/react-query"


export function useEvents() {
    // const result = inboundOrderQuery.safeParse(filtros)

    // const filtrosValidados = result.success ? result.data : {}

    return useQuery<Event[]>({
        queryKey: ["events"],
        queryFn: async () => {
            const { data } = await eventService.getAll();
            return data.data;
        },
        placeholderData: (prev: any) => prev,
        staleTime: 1000 * 60 * 30,
    })
}