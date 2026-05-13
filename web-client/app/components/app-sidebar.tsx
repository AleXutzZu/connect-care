import * as React from "react"
import {NavMain} from "~/components/nav-main"
import {NavUser} from "~/components/nav-user"
import {
    Sidebar,
    SidebarContent,
    SidebarFooter,
    SidebarHeader,
    SidebarMenu,
    SidebarMenuButton,
    SidebarMenuItem,
} from "~/components/ui/sidebar"
import {ChartBarIcon, CommandIcon, LayoutDashboardIcon, ListIcon, UsersIcon} from "lucide-react"
import {Link} from "react-router";

const data = {
    user: {
        name: "shadcn",
        email: "m@example.com",
        avatar: "/avatars/shadcn.jpg",
    },
    navMain: [
        {
            title: "Dashboard",
            url: "/dashboard",
            icon: (
                <LayoutDashboardIcon
                />
            ),
        },
        {
            title: "Donors",
            url: "/dashboard/donors",
            icon: (
                <ListIcon
                />
            ),
        },
        {
            title: "Donations",
            url: "/dashboard/donations",
            icon: (
                <ChartBarIcon
                />
            ),
        },
        {
            title: "Users",
            url: "/dashboard/users",
            icon: (
                <UsersIcon
                />
            ),
        },
    ],
}

export function AppSidebar({...props}: React.ComponentProps<typeof Sidebar>) {
    return (
        <Sidebar collapsible="offcanvas" {...props}>
            <SidebarHeader>
                <SidebarMenu>
                    <SidebarMenuItem>
                        <SidebarMenuButton
                            asChild
                            className="data-[slot=sidebar-menu-button]:p-1.5!"
                        >
                            <Link to="/dashboard">
                                <CommandIcon className="size-5!"/>
                                <span className="text-base font-semibold">Teledon</span>
                            </Link>
                        </SidebarMenuButton>
                    </SidebarMenuItem>
                </SidebarMenu>
            </SidebarHeader>
            <SidebarContent>
                <NavMain items={data.navMain}/>
            </SidebarContent>
            <SidebarFooter>
                <NavUser user={data.user}/>
            </SidebarFooter>
        </Sidebar>
    )
}
