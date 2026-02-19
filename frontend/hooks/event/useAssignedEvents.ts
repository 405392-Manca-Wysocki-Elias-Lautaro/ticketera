import { eventService } from '@/services/eventService'
import { EventInfo } from "@/services/eventService"
import { useQuery } from "@tanstack/react-query"

export function useAssignedEvents() {
    return useQuery<EventInfo[]>({
        queryKey: ["assigned-events"],
        queryFn: async () => {
            const { data } = await eventService.getAssignedEvents();
            return data.data;
        },
        placeholderData: (prev: any) => prev,
        staleTime: 1000 * 60 * 5, // 5 minutes
    })
}
