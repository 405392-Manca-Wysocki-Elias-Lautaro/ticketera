export interface ApiErrorResponse {
    data?: {
        code?: string
        message?: string
        details?: Record<string, any>
    }
    status?: number
}