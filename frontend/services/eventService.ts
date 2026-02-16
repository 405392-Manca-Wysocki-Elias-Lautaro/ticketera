import api from "@/lib/api";
import type { ApiResponse } from "@/types/Response/ApiResponse";
import type { OrganizerMetrics } from "@/types/OrganizerMetrics";
import { CreateEvent } from '@/types/Request/CreateEvent';
import { createCrudService } from "./createCrud";

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

const BASE_URL = "/events";

export const eventService = {

    ...createCrudService<Event>(BASE_URL),
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
    },

    /**
     * Obtiene las métricas del organizador
     */
    async getOrganizerMetrics(): Promise<OrganizerMetrics | null> {
        try {
            console.log("🔍 Fetching organizer metrics...");
            const response = await api.get<ApiResponse<OrganizerMetrics>>("/events/metrics");
            console.log("✅ Metrics response:", response.data);
            return response.data.data;
        } catch (error: any) {
            console.error("❌ Error fetching organizer metrics:", error);
            if (error.response) {
                console.error("Response status:", error.response.status);
                console.error("Response data:", error.response.data);
            }
            return null;
        }
    },

    create: (data: CreateEvent) => api.post(`${BASE_URL}`, data),

    getAllCategories: () => api.get(`${BASE_URL}/categories`),

    // Staff management
    assignStaff: (eventId: string, userId: string) => api.post<ApiResponse<void>>(`${BASE_URL}/${eventId}/staff`, { userId }),
    removeStaff: (eventId: string, userId: string) => api.delete<ApiResponse<void>>(`${BASE_URL}/${eventId}/staff/${userId}`),
    getEventStaff: (eventId: string) => api.get<ApiResponse<string[]>>(`${BASE_URL}/${eventId}/staff`),

    // Organization events
    getMyOrganizationEvents: () => api.get<ApiResponse<EventInfo[]>>(`${BASE_URL}/my-organization`),


};

