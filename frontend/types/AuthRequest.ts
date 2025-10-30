import { User } from './User'

export interface LoginRequest {
    email: string
    password: string
    remembered: boolean
}

