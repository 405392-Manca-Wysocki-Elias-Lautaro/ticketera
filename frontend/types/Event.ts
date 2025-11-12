import { EventArea } from './Areas'
import { Category } from "./Category"

export interface Event {
    id: string
    organizerId: string
    title: string
    slug: string
    description: string
    coverUrl: string
    status: string
    categoryId: string
    categoryName: string
    venueName: string
    addressLine: string
    city: string
    state: string
    country: string
    lat: number
    lng: number
    startsAt: string
    endsAt: string
    minPriceCents: number
    currency: string
    totalAvailableTickets: number
    totalCapacity: number

    // Relación opcional
    category?: Category
    areas: EventArea[]
}
