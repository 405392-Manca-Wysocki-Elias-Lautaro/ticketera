import { TicketStatus } from './enums/TicketStatus';
import { Event } from './Event';
import { EventArea } from './EventArea';

export interface Ticket {
    id: string
    orderItemId: string
    eventId: string
    eventVenueAreaId: string
    eventVenueSeatId: string
    userId: string
    code: string
    qrBase64: string
    price: number
    currency: string
    discount: number
    finalPrice: number
    status: TicketStatus
    issuedAt: string
    checkedInAt: string
    canceledAt: string
    refundedAt: string
    event: EventWithArea
}

export interface EventWithArea extends Event {
    area: EventArea;
}
