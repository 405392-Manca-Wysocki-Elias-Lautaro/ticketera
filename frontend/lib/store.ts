import { User } from '@/types/User';
import { create } from "zustand";
import { persist } from "zustand/middleware";

interface AuthState {
    token: string | null;
    user: User | null;
    isLoading: boolean;
    setToken: (token: string | null) => void;
    setUser: (user: User | null) => void;
    setSessionFlag: (flag: boolean) => void;
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
            setSessionFlag: (flag) => {
                if (flag) {
                    document.cookie = "sessionFlag=true; Path=/; SameSite=Strict;";
                    localStorage.setItem("sessionFlag", "true");
                } else {
                    document.cookie = "sessionFlag=; Max-Age=0; Path=/;";
                    localStorage.removeItem("sessionFlag");
                }
            },

            logout: () => {
                set({ token: null, user: null });
                localStorage.removeItem("sessionFlag");
                document.cookie = "sessionFlag=; Max-Age=0; path=/;";
            }
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
