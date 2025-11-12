import { eventService } from '@/services/eventService';
import { Event } from '@/types/Event';
import { useQuery, type UseQueryOptions, type UseQueryResult } from "@tanstack/react-query";

export function useEvent<TData = Event>(
    id?: number | null,
    options?: Omit<UseQueryOptions<Event, Error, TData>, "queryKey" | "queryFn">
): UseQueryResult<TData, Error> {
    return useQuery<Event, Error, TData>({
        queryKey: ["event", id],
        queryFn: async () => {
            const { data } = await eventService.getById(id!);
            return data.data;
        },
        enabled: !!id,
        ...options,
    });
}
