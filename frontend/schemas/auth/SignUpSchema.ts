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
    role: z.enum(["CUSTOMER", "ADMIN"]),
    confirmPassword: z.string(),
    termsAccepted: z.boolean().refine((val) => val === true, {
        message: "Debes aceptar los términos y condiciones",
    }),
    organizationName: z.string().optional(),
    organizationAddress: z.string().optional(),
}).superRefine((data, ctx) => {
    if (data.password !== data.confirmPassword) {
        ctx.addIssue({
            code: z.ZodIssueCode.custom, // Use generic error code for simplicity
            message: "Las contraseñas no coinciden",
            path: ["confirmPassword"],
        });
    }

    if (data.role === "ADMIN") {
        if (!data.organizationName || data.organizationName.trim().length === 0) {
            ctx.addIssue({
                code: z.ZodIssueCode.custom,
                message: "El nombre de la organización es obligatorio",
                path: ["organizationName"],
            });
        }
        if (!data.organizationAddress || data.organizationAddress.trim().length === 0) {
            ctx.addIssue({
                code: z.ZodIssueCode.custom,
                message: "La dirección de la organización es obligatoria",
                path: ["organizationAddress"],
            });
        }
    }
});

export type SignUpSchema = z.infer<typeof signUpSchema>;
