import { Event } from '@/types/Event'
import { createCrudService } from './createCrud'

const BASE_URL = "/events"

export const eventService = {
    ...createCrudService<Event>(BASE_URL)
}