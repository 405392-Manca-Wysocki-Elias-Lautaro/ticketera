import api from '@/lib/api';
import { ApiResponse } from '@/types/Response/ApiResponse';

export const reminderService = {
    /**
     * Trigger event reminders manually.
     * @param simulatedTime Optional ISO date string to simulate "now".
     */
    triggerReminders: async (simulatedTime?: string): Promise<string> => {
        const response = await api.post<string>('/tickets/api/debug/reminders/trigger', null, {
            params: simulatedTime ? { simulatedTime } : undefined
        });
        // The backend returns a String message, not wrapped in ApiResponse standard wrapper in this specific debug controller
        return response.data;
    }
};
