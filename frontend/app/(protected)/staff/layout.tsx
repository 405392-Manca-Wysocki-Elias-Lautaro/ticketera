import { StaffSidebar } from '@/components/StaffSidebar';

export default function StaffLayout({ children }: { children: React.ReactNode; }) {
    return (

        <div className="flex h-screen w-screen overflow-hidden">
            <StaffSidebar />
            <div className="flex w-full h-screen justify-center items-start pb-5">
                {children}
            </div>
        </div>
    )
}