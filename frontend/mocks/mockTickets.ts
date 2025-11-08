export interface Ticket {
    id: string
    eventId: string
    eventTitle: string
    eventDate: string
    eventTime: string
    eventLocation: string
    areaName: string
    seatNumber?: string
    price: number
    qrCode: string
    status: "valid" | "used"
}

export const mockTickets: Ticket[] = [
    {
        id: "t1",
        eventId: "1",
        eventTitle: "Festival de Rock 2025",
        eventDate: "2025-11-20T18:00:00",
        eventTime: "18:00",
        eventLocation: "Estadio Ciudad de La Plata",
        areaName: "Campo VIP",
        price: 35000,
        qrCode: "ROCK-2025-VIP-001",
        status: "valid",
    },
    {
        id: "t2",
        eventId: "2",
        eventTitle: "Rock & Lights Experience",
        eventDate: "2025-12-10T21:00:00",
        eventTime: "21:00",
        eventLocation: "Luna Park, Buenos Aires",
        areaName: "Platea Baja",
        seatNumber: "Fila B - Asiento 22",
        price: 20000,
        qrCode: "LIGHTS-BAJA-022",
        status: "valid",
    },
    {
        id: "t3",
        eventId: "6",
        eventTitle: "Jazz Íntimo en Palermo",
        eventDate: "2025-11-28T22:00:00",
        eventTime: "22:00",
        eventLocation: "Thelonious Club, Palermo",
        areaName: "Mesa 3",
        seatNumber: "Asiento 5",
        price: 12000,
        qrCode: "JAZZ-MESA3-005",
        status: "valid",
    },
    {
        id: "t4",
        eventId: "10",
        eventTitle: "Obra de Teatro 'Sombras del Tiempo'",
        eventDate: "2025-11-27T20:00:00",
        eventTime: "20:00",
        eventLocation: "Teatro San Martín, Buenos Aires",
        areaName: "Platea Central",
        seatNumber: "Fila A - Asiento 14",
        price: 10000,
        qrCode: "TEATRO-ST-014",
        status: "used",
    },
    {
        id: "t5",
        eventId: "5",
        eventTitle: "Partido Final Copa de Oro",
        eventDate: "2025-12-12T17:00:00",
        eventTime: "17:00",
        eventLocation: "Estadio Único Madre de Ciudades",
        areaName: "Popular",
        price: 10000,
        qrCode: "COPA-ORO-2025-POP-321",
        status: "used",
    },
]
