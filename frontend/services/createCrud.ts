import api from '@/lib/api';

export function createCrudService(baseUrl: string) {
    return {
        getAll: (params?: any) => api.get(baseUrl, { params }),
        getById: (id: number) => api.get(`${baseUrl}/${id}`),
        create: (data: any) => api.post(baseUrl, data),
        update: (id: number, data: any) => api.put(`${baseUrl}/${id}`, data),
        delete: (id: number) => api.delete(`${baseUrl}/${id}`)
    };
}
