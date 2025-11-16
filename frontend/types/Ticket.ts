export enum TicketStatus {
    ISSUED = 'ISSUED',
    CHECKED_IN = 'CHECKED_IN',
    CANCELED = 'CANCELED',
    REFUNDED = 'REFUNDED'
}

export interface TicketResponse {
    id: string;
    orderItemId: string;
    occurrenceId: string;
    eventVenueAreaId?: string;
    eventVenueSeatId?: string;
    userId: string;
    code: string;
    qrBase64?: string;
    status: TicketStatus;
    issuedAt: string;
    checkedInAt?: string;
    canceledAt?: string;
    refundedAt?: string;
    // Información enriquecida del evento (temporal - hasta que implementemos backend completo)
    eventTitle?: string;
    eventLocation?: string;
    eventDate?: string;
}

// Interface extendida con información del evento (para mostrar en el frontend)
export interface TicketWithEventInfo extends TicketResponse {
    eventTitle?: string;
    eventDate?: string;
    eventTime?: string;
    eventLocation?: string;
    areaName?: string;
    seatNumber?: string;
    price?: number;
}