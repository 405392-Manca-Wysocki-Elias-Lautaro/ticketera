export interface CreateEvent {
    organizerId: string
    title: string
    slug: string
    description: string
    categoryId: string
    coverUrl: string
    status: string
    venueName: string
    venueDescription: string
    addressLine: string
    city: string
    state: string
    country: string
    startsAt: string
    endsAt: string
    areas: CreateArea[]
}

export interface CreateArea {
    name: string
    isGeneralAdmission: boolean
    capacity: number
    position: number
    priceCents: number
    seats: CreateSeat[]
}

export interface CreateSeat {
    seatNumber: number
    rowNumber: number
    label: string
}

