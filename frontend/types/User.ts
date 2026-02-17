import { Role } from './Role'

export interface User {
    id: string
    organizationId?: string
    firstName: string
    lastName: string
    email: string
    emailVerified: boolean
    active: boolean
    id_role: number
    role: Role
}
