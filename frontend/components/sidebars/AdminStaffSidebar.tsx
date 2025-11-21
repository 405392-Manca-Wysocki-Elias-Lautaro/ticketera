"use client"

import Link from "next/link"
import { usePathname } from "next/navigation"
import GradientText from "@/components/GradientText"

import {
    Sidebar,
    SidebarContent,
    SidebarGroup,
    SidebarGroupContent,
    SidebarGroupLabel,
    SidebarHeader,
    SidebarMenu,
    SidebarMenuButton,
    SidebarMenuItem,
    SidebarRail,
} from "@/components/ui/sidebar"

import { SidebarConfig } from "./sidebar.config"
import { RoleUtils } from "@/utils/roleUtils"

export function AdminStaffSidebar({ user }: { user: any }) {
    const pathname = usePathname()

    const isSuper = RoleUtils.isSuperAdmin(user)
    const isAdmin = isSuper || RoleUtils.isAdmin(user)
    const isStaff = isSuper || RoleUtils.isStaff(user)

    let menu: any[] = []

    if (RoleUtils.isSuperAdmin(user)) {
        menu = [...SidebarConfig.admin, ...SidebarConfig.staff]
    } else if (RoleUtils.isAdmin(user)) {
        menu = [...SidebarConfig.admin]
    } else if (RoleUtils.isStaff(user)) {
        menu = [...SidebarConfig.staff]
    }

    const panelTitle = (isAdmin || isSuper) ? "Panel Admin" : "Panel Staff"

    return (
        <Sidebar collapsible="icon" variant="sidebar" className="border-r">
            <SidebarHeader>
                <GradientText>
                    <h2 className="text-lg font-semibold">{panelTitle}</h2>
                </GradientText>
            </SidebarHeader>

            <SidebarContent>
                <SidebarGroup>
                    <SidebarGroupLabel>Menú</SidebarGroupLabel>
                    <SidebarGroupContent>
                        <SidebarMenu>
                            {menu.map((link) =>  (
                                    <SidebarMenuItem key={link.href}>
                                        <SidebarMenuButton
                                            asChild
                                            isActive={pathname === link.href}
                                            tooltip={link.title}
                                        >
                                            <Link href={link.href}>
                                                <link.icon className="mr-2 h-4 w-4" />
                                                <span>{link.title}</span>
                                            </Link>
                                        </SidebarMenuButton>
                                    </SidebarMenuItem>
                                )
                            )}
                        </SidebarMenu>
                    </SidebarGroupContent>
                </SidebarGroup>
            </SidebarContent>

            <SidebarRail />
        </Sidebar>
    )
}
