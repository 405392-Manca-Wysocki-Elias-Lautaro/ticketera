import { Category } from './Category'

export interface Event {
    id: number
    organizerId: number
    title: string
    slug: string
    description: string
    categoryId: number
    coverUrl: string
    status: string
    category: Category
}