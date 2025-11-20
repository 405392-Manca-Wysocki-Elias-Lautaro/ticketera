import { eventService } from '@/services/eventService'
import { useMutation, useQueryClient } from '@tanstack/react-query'

export const useCreateEvent = () => {
    const queryClient = useQueryClient()

    return useMutation({
        mutationFn: eventService.create,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['events'] })
        }
    })
}