import * as z from "zod";

export const signUpSchema = z.object({
    firstName: z.string().min(1, "El nombre es obligatorio"),
    lastName: z.string().min(1, "El apellido es obligatorio"),
    email: z.email("Email inválido"),
    password: z
        .string()
        .min(8, "La contraseña debe tener al menos 8 caracteres")
        .max(64, "La contraseña no puede tener más de 64 caracteres")
        .regex(/[a-z]/, "Debe contener una letra minúscula")
        .regex(/[A-Z]/, "Debe contener una letra mayúscula")
        .regex(/\d/, "Debe contener un número")
        .regex(/[@$!%*?&]/, "Debe contener un caracter especial (@$!%*?&)"),
    confirmPassword: z.string(),
}).refine((data) => data.password === data.confirmPassword, {
    message: "Las contraseñas no coinciden",
    path: ["confirmPassword"],
});

export type SignUpSchema = z.infer<typeof signUpSchema>;
