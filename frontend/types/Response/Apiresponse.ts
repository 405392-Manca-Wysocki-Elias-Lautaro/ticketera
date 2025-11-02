import { AuthResponse } from './AuthResponse'

export interface ApiResponse {
    data: AuthResponse
    message: string
    success: boolean
    timestamp: string
}