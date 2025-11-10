import { RoleCode } from './enums/RoleCode'

export interface Role {
    id: string
    code: RoleCode
    name: string
    description: string
}