"use client";
import { ChangeEvent } from "react";

export default function useDecimalFormatter() {
    const formatDecimal = (value: string): string => {
        if (!value) return "";

        // eliminar caracteres raros
        value = value.replace(/,/g, ".").replace(/[^0-9.]/g, "");

        // si hay más de un punto, quedate solo con el primero
        const parts = value.split(".");
        if (parts.length > 2) {
            value = parts[0] + "." + parts.slice(1).join("");
        }

        // convertir a número
        const num = Number(value);
        if (isNaN(num)) return "";

        // formatear con dos decimales
        return num.toFixed(2);
    };

    const handleDecimalInput = (
        e: ChangeEvent<HTMLInputElement>,
        onChange: (value: any) => void
    ) => {
        const formatted = formatDecimal(e.target.value);
        onChange(formatted);
    };

    return { handleDecimalInput };
}
