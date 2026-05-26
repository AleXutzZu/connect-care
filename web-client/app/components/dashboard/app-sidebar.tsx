import * as React from "react"
import {NavMain} from "~/components/dashboard/nav-main"
import {NavUser} from "~/components/dashboard/nav-user"
import {
    Sidebar,
    SidebarContent,
    SidebarFooter,
    SidebarHeader,
    SidebarMenu,
    SidebarMenuButton,
    SidebarMenuItem,
} from "~/components/ui/sidebar"
import {CommandIcon, LayoutDashboardIcon, ListIcon} from "lucide-react"
import {Link} from "react-router";

const links = {
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
        /*{
            title: "Users",
            url: "/dashboard/users",
            icon: (
                <UsersIcon
                />
            ),
        },*/
    ],
}

export function AppSidebar({user, ...props}: React.ComponentProps<typeof Sidebar> & {user : {username: string}}) {
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
                <NavMain items={links.navMain}/>
            </SidebarContent>
            <SidebarFooter>
                <NavUser user={user}/>
            </SidebarFooter>
        </Sidebar>
    )
}
