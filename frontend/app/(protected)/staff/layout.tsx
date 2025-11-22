'use client'

import { Navbar } from '@/components/Navbar';
import { AdminStaffSidebar } from '@/components/sidebars/AdminStaffSidebar';
import { SidebarInset, SidebarTrigger } from '@/components/ui/sidebar';
import { useAuthStore } from '@/lib/store';

export default function StaffLayout({ children }: { children: React.ReactNode }) {

    const {user} = useAuthStore();

    return (
        <div className="flex w-screen h-[100dvh] overflow-hidden">
            <AdminStaffSidebar user={user} />

            <SidebarInset className="flex flex-col w-full min-h-screen">

                <Navbar
                    leftSlot={
                        <div className="md:hidden">
                            <SidebarTrigger />
                        </div>
                    }
                    hideSearchOn={[
                        "/staff",
                        "/staff/events",
                    ]}

                />

                <div className="flex flex-col w-full h-full pb-5 overflow-y-auto">
                    {children}
                </div>

            </SidebarInset>
        </div>
    )
}
