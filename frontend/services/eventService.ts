import { Event } from '@/types/Event'
import { createCrudService } from './createCrud'
import api from '@/lib/api'
import { CreateEvent } from '@/types/Request/CreateEvent'

const BASE_URL = "/events"

export const eventService = {
    ...createCrudService<Event>(BASE_URL),

    create: (data: CreateEvent) => api.post(`${BASE_URL}`, data),

    getAllCategories: () => api.get(`${BASE_URL}/categories`)
}