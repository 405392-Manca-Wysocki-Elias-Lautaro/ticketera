import { Event } from '@/types/Event'
import { createCrudService } from './createCrud'
import api from '@/lib/api'

const BASE_URL = "/events"

export const eventService = {
    ...createCrudService<Event>(BASE_URL),

    getAllCategories: () => api.get(`${BASE_URL}/categories`)
}