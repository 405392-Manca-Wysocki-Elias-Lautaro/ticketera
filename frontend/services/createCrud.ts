import api from '@/lib/api';
import { ApiResponse } from '@/types/Response/ApiResponse';

export function createCrudService<TData, TParams = any>(baseUrl: string) {
    return {
        getAll: (params?: TParams) =>
            api.get<ApiResponse<TData>>(baseUrl, { params }),

        getById: (id: number | string) =>
            api.get<ApiResponse<TData>>(`${baseUrl}/${id}`),

        create: (data: TData) =>
            api.post<ApiResponse<TData>>(baseUrl, data),

        update: (id: number | string, data: TData) =>
            api.put<ApiResponse<TData>>(`${baseUrl}/${id}`, data),

        delete: (id: number | string) =>
            api.delete<void>(`${baseUrl}/${id}`),
    }
}