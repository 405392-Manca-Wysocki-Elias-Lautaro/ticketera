export interface RowForm {
    id: string
    name: string
    capacity: number
    seats: number
    startSeat: number
    endSeat: number
}

export interface EventArea {
    id: string
    name: string
    price: number
    type: "general" | "numbered"
    rows: RowForm[]
    capacity: number
}

export interface Event {
    id: string
    title: string
    description: string
    category: string
    date: string
    time: string
    location: string
    address: string
    availableTickets: number
    price: number
    image: string
    areas: EventArea[]
}

export const mockEvents: Event[] = [
    {
        id: "1",
        title: "Festival de Rock 2025",
        description:
            "Tres días de pura energía con las mejores bandas nacionales e internacionales. Escenarios múltiples, luces y sonido espectaculares.",
        category: "Música",
        date: "2025-11-20T18:00:00",
        time: "18:00",
        location: "Estadio Ciudad de La Plata",
        address: "Av. 25 y 32, La Plata, Buenos Aires",
        availableTickets: 5000,
        price: 25000,
        image: "/rock-festival-concert-stage-lights-crowd.jpg",
        areas: [
            { id: "a1", name: "Campo VIP", price: 35000, type: "general", capacity: 2000, rows: [] },
            { id: "a2", name: "Campo General", price: 25000, type: "general", capacity: 3000, rows: [] },
        ],
    },
    {
        id: "2",
        title: "Rock & Lights Experience",
        description:
            "Una experiencia visual y sonora única: luces, pantallas y la mejor música en vivo para los amantes del rock moderno.",
        category: "Música",
        date: "2025-12-10T21:00:00",
        time: "21:00",
        location: "Luna Park",
        address: "Av. Madero 420, Buenos Aires",
        availableTickets: 1800,
        price: 20000,
        image: "/rock-festival-stage-lights.jpg",
        areas: [
            {
                id: "a1", name: "Platea Baja", price: 20000, type: "numbered", capacity: 500, rows: [
                    { id: "r1", name: "Fila A", capacity: 50, seats: 50, startSeat: 1, endSeat: 50 },
                    { id: "r2", name: "Fila B", capacity: 50, seats: 50, startSeat: 51, endSeat: 100 },
                ]
            },
            { id: "a2", name: "Campo", price: 15000, type: "general", capacity: 1000, rows: [] },
        ],
    },
    {
        id: "3",
        title: "Comedy Night Live",
        description:
            "Una noche para reír sin parar con los mejores comediantes del país. Humor, improvisación y mucho carisma.",
        category: "Comedia",
        date: "2025-11-30T20:30:00",
        time: "20:30",
        location: "Teatro Metropolitan",
        address: "Av. Corrientes 1343, Buenos Aires",
        availableTickets: 350,
        price: 8000,
        image: "/comedy-show-stage.png",
        areas: [
            {
                id: "a1", name: "Platea", price: 8000, type: "numbered", capacity: 300, rows: [
                    { id: "r1", name: "Fila A", capacity: 20, seats: 20, startSeat: 1, endSeat: 20 },
                    { id: "r2", name: "Fila B", capacity: 20, seats: 20, startSeat: 21, endSeat: 40 },
                ]
            },
        ],
    },
    {
        id: "4",
        title: "Cine Arte Festival",
        description:
            "Proyecciones exclusivas de cine independiente y nacional, con la presencia de directores y actores invitados.",
        category: "Cine",
        date: "2025-12-05T19:00:00",
        time: "19:00",
        location: "Cinemark Palermo",
        address: "Beruti 3399, Buenos Aires",
        availableTickets: 120,
        price: 5000,
        image: "/film-festival-cinema.png",
        areas: [
            {
                id: "a1", name: "Sala Principal", price: 5000, type: "numbered", capacity: 120, rows: [
                    { id: "r1", name: "Fila A", capacity: 12, seats: 12, startSeat: 1, endSeat: 12 },
                    { id: "r2", name: "Fila B", capacity: 12, seats: 12, startSeat: 13, endSeat: 24 },
                ]
            },
        ],
    },
    {
        id: "5",
        title: "Partido Final Copa de Oro",
        description:
            "Vení a vivir la pasión del fútbol en la gran final de la Copa de Oro 2025. Un espectáculo deportivo imperdible.",
        category: "Deportes",
        date: "2025-12-12T17:00:00",
        time: "17:00",
        location: "Estadio Único Madre de Ciudades",
        address: "Av. Diego Maradona s/n, Santiago del Estero",
        availableTickets: 8000,
        price: 10000,
        image: "/football-soccer-stadium-match-crowd.jpg",
        areas: [
            { id: "a1", name: "Popular", price: 10000, type: "general", capacity: 4000, rows: [] },
            {
                id: "a2", name: "Platea", price: 15000, type: "numbered", capacity: 4000, rows: [
                    { id: "r1", name: "Fila A", capacity: 80, seats: 80, startSeat: 1, endSeat: 80 },
                    { id: "r2", name: "Fila B", capacity: 80, seats: 80, startSeat: 81, endSeat: 160 },
                ]
            },
        ],
    },
    {
        id: "6",
        title: "Jazz Intimo en Palermo",
        description:
            "Un concierto cálido con artistas destacados del jazz argentino en un ambiente íntimo con piano y saxofón.",
        category: "Música",
        date: "2025-11-28T22:00:00",
        time: "22:00",
        location: "Thelonious Club",
        address: "Jerónimo Salguero 1884, Buenos Aires",
        availableTickets: 90,
        price: 12000,
        image: "/jazz-concert-intimate-venue-saxophone-piano.jpg",
        areas: [
            {
                id: "a1", name: "Mesas", price: 12000, type: "numbered", capacity: 90, rows: [
                    { id: "r1", name: "Mesa 1", capacity: 10, seats: 10, startSeat: 1, endSeat: 10 },
                    { id: "r2", name: "Mesa 2", capacity: 10, seats: 10, startSeat: 11, endSeat: 20 },
                ]
            },
        ],
    },
    {
        id: "7",
        title: "Orquesta Sinfónica Nacional",
        description:
            "Un viaje musical con las obras más icónicas interpretadas por la Orquesta Sinfónica Nacional.",
        category: "Música",
        date: "2025-12-15T20:00:00",
        time: "20:00",
        location: "Teatro Colón",
        address: "Cerrito 628, Buenos Aires",
        availableTickets: 500,
        price: 18000,
        image: "/orchestra-concert-hall.jpg",
        areas: [
            { id: "a1", name: "Platea Central", price: 18000, type: "numbered", capacity: 300, rows: [] },
            { id: "a2", name: "Palcos", price: 25000, type: "numbered", capacity: 200, rows: [] },
        ],
    },
    {
        id: "8",
        title: "Conferencia Innovatech 2025",
        description:
            "El evento tecnológico más importante del año. Charlas, paneles y networking con líderes de la industria.",
        category: "Tecnología",
        date: "2025-12-01T09:00:00",
        time: "09:00",
        location: "Centro de Convenciones Buenos Aires",
        address: "Av. Figueroa Alcorta 2099, CABA",
        availableTickets: 1000,
        price: 15000,
        image: "/tech-conference-modern.jpg",
        areas: [
            { id: "a1", name: "General", price: 15000, type: "general", capacity: 800, rows: [] },
            { id: "a2", name: "VIP", price: 25000, type: "numbered", capacity: 200, rows: [] },
        ],
    },
    {
        id: "9",
        title: "Charlas de Negocios LATAM",
        description:
            "Los referentes más influyentes de la región se reúnen para hablar sobre innovación y crecimiento empresarial.",
        category: "Tecnología",
        date: "2025-12-03T10:00:00",
        time: "10:00",
        location: "Hotel Hilton Puerto Madero",
        address: "Macacha Güemes 351, Buenos Aires",
        availableTickets: 300,
        price: 20000,
        image: "/tech-conference-presentation-modern-auditorium.jpg",
        areas: [
            { id: "a1", name: "Auditorio", price: 20000, type: "numbered", capacity: 300, rows: [] },
        ],
    },
    {
        id: "10",
        title: "Obra de Teatro 'Sombras del Tiempo'",
        description:
            "Una puesta en escena impactante sobre la memoria y las segundas oportunidades.",
        category: "Teatro",
        date: "2025-11-27T20:00:00",
        time: "20:00",
        location: "Teatro San Martín",
        address: "Av. Corrientes 1530, Buenos Aires",
        availableTickets: 250,
        price: 10000,
        image: "/theater-play-stage-performance-dramatic-lighting.jpg",
        areas: [
            { id: "a1", name: "Platea Central", price: 10000, type: "numbered", capacity: 250, rows: [] },
        ],
    },
]
