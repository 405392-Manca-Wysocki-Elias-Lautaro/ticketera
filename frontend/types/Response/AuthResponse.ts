import { User } from '../User'

export interface AuthResponse {
    accessToken: string
    expiresIn: string
    user: User
    tokenType: string
    deviceId: string
}

