import { z } from "zod"

export const loginSchema = z.object({
    email: z.email("Debe ser un email válido"),
    password: z.string().min(6, "La contraseña debe tener al menos 6 caracteres"),
    remembered: z.boolean(),
})

export type LoginSchema = z.infer<typeof loginSchema>
