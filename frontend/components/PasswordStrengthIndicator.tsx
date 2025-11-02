"use client";
import { Check, X } from "lucide-react";

interface Props {
    password?: string;
}

const PasswordStrengthIndicator = ({ password = "" }: Props) => {
    const rules = [
        { label: "8-64 caracteres", valid: password.length >= 8 && password.length <= 64 },
        { label: "Una letra mayúscula", valid: /[A-Z]/.test(password) },
        { label: "Una letra minúscula", valid: /[a-z]/.test(password) },
        { label: "Un número", valid: /\d/.test(password) },
        { label: "Un caracter especial (@$!%*?&)", valid: /[@$!%*?&]/.test(password) },
    ];

    const strength = rules.filter(r => r.valid).length;

    const getColor = () => {
        if (strength <= 2) return "bg-red-500";
        if (strength === 3 || strength === 4) return "bg-yellow-500";
        return "bg-green-500";
    };

    return (
        <div className="mt-2 space-y-1">
            <div className="w-full h-2 rounded bg-gray-200 overflow-hidden">
                <div className={`h-2 ${getColor()} transition-all duration-300`} style={{ width: `${(strength / 5) * 100}%` }} />
            </div>
            <ul className="text-xs mt-1 space-y-0.5">
                {rules.map((r) => (
                    <li key={r.label} className={`flex items-center gap-1 ${r.valid ? "text-green-600" : "text-gray-500"}`}>
                        {r.valid ? <Check size={12} /> : <X size={12} />}
                        {r.label}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default PasswordStrengthIndicator;
