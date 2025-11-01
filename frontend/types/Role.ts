import { RoleCode } from './enums/roleCode'

export interface Role {
    id: string
    code: RoleCode
    name: string
    description: string
}