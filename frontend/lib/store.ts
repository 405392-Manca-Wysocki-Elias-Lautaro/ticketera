import { User } from '@/types/User';
import { create } from "zustand";
import { persist } from "zustand/middleware";

interface AuthState {
    token: string | null;
    user: User | null;
    isLoading: boolean;
    setToken: (token: string | null) => void;
    setUser: (user: User | null) => void;
    setLoading: (loading: boolean) => void;
    logout: () => void;
}

export const useAuthStore = create(
    persist<AuthState>(
        (set) => ({
            token: null,
            user: null,
            isLoading: true,

            setToken: (token) => set({ token }),
            setUser: (user) => set({ user }),
            setLoading: (loading) => set({ isLoading: loading }),

            logout: () => {
                set({ token: null, user: null });
            },
        }),
        {
            name: "auth-storage",

            onRehydrateStorage: () => (state) => {
                if (state) {
                    setTimeout(() => {
                        state.setLoading(false);
                    }, 0);
                }
            },
        }
    )
);
