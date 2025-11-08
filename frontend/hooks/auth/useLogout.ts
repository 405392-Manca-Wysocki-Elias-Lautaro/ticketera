import { useMutation } from "@tanstack/react-query";
import { authService } from "@/services/authService";
import type { AxiosError } from "axios";

export function useLogout() {
    return useMutation<void, AxiosError>({
        mutationFn: async () => {
            await authService.logout();
        }
    });
}
