import { createCrudService } from './createCrud'

const BASE_URL = "/events"

export const eventService = {
    ...createCrudService<>(BASE_URL)
}