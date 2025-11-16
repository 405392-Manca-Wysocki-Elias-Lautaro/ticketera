import api from "@/lib/api";
import type { ApiResponse } from "@/types/Response/ApiResponse";

// Interface básica del evento (puedes expandirla según necesites)
export interface EventInfo {
    id: string;
    title: string;
    description?: string;
    coverUrl?: string;
    venueName?: string;
    addressLine?: string;
    city?: string;
    state?: string;
    country?: string;
    startsAt?: string;
    endsAt?: string;
}

export const eventService = {
    /**
     * Obtiene información básica de un evento por su ID
     */
    async getEventById(eventId: string): Promise<EventInfo | null> {
        try {
            const response = await api.get<ApiResponse<EventInfo>>(`/events/${eventId}`);
            return response.data.data;
        } catch (error) {
            console.error(`Error fetching event ${eventId}:`, error);
            return null;
        }
    },

    /**
     * Obtiene información de múltiples eventos
     */
    async getEventsByIds(eventIds: string[]): Promise<Map<string, EventInfo>> {
        const eventsMap = new Map<string, EventInfo>();
        
        // Fetch all events in parallel
        const promises = eventIds.map(id => this.getEventById(id));
        const results = await Promise.all(promises);
        
        results.forEach((event, index) => {
            if (event) {
                eventsMap.set(eventIds[index], event);
            }
        });
        
        return eventsMap;
    }
};

