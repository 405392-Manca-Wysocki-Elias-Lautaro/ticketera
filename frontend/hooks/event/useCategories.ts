import { eventService } from '@/services/eventService'
import { Category } from '@/types/Category'
import { useQuery } from "@tanstack/react-query"

export function useCategories() {
    return useQuery<Category[]>({
        queryKey: ["categories"],
        queryFn: async () => {
            const { data } = await eventService.getAllCategories();
            return data.data;
        },
        placeholderData: (prev: any) => prev,
        staleTime: Infinity,
    })
}