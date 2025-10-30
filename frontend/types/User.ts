import { Role } from './Role'

export interface User {
    id: string
    firstName: string
    lastName: string
    email: string
    emailVerified: boolean
    active: boolean
    id_role: number
    rol: Role
}
