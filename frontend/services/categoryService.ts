import { Category } from '@/types/Category'
import { createCrudService } from './createCrud'

const BASE_URL = "/categories"

export const categoryService = {
    ...createCrudService<Category>(BASE_URL)
}