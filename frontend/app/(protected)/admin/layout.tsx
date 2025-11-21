'use client'

import { Navbar } from '@/components/Navbar';
import { AdminStaffSidebar } from '@/components/sidebars/AdminStaffSidebar';
import { SidebarInset, SidebarTrigger } from '@/components/ui/sidebar';
import { useAuthStore } from '@/lib/store';

export default function AdminLayout({ children }: { children: React.ReactNode; }) {

    const { user } = useAuthStore();

    return (

        <div className="flex w-screen h-[100dvh] overflow-hidden">
            <AdminStaffSidebar user={user} />
            <SidebarInset>

                <div className='flex flex-col h-full'>
                    <Navbar
                        leftSlot={
                            <div className="md:hidden">
                                <SidebarTrigger />
                            </div>
                        }
                        hideSearchOn={[
                            "/admin",
                            "/admin/events",
                            "/admin/events/create",
                            "/admin/events/edit",
                            "/admin/payments",
                            "/admin/settings",
                            "/admin/validate",
                        ]}

                    />

                    <div className="flex flex-col w-full h-full pb-5 overflow-y-auto">
                        {children}
                    </div>
                </div>
            </SidebarInset>

        </div>
    )
}