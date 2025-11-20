import { ticketService } from '@/services/ticketService'
import { useMutation } from '@tanstack/react-query'

export const useValidateTicket = () => {
    return useMutation({
        mutationFn: ticketService.validate,
    })
}