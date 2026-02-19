export interface ValidateTicket {
    type: "QR" | "CODE"
    value: string
    eventId: string
}