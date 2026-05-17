import {Avatar, AvatarFallback,} from "~/components/ui/avatar"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "~/components/ui/dropdown-menu"
import {SidebarMenu, SidebarMenuButton, SidebarMenuItem, useSidebar,} from "~/components/ui/sidebar"
import {EllipsisVerticalIcon, LogOutIcon} from "lucide-react"
import {useFetcher} from "react-router";
import {useRef} from "react";

function getInitials(name: string) {
    if (!name) return "";

    return name
        .trim()
        .split(/\s+/)
        .map(word => word[0])
        .join("")
        .toUpperCase()
        .slice(0, 2);
}

export function NavUser({
                            user,
                        }: {
    user: {
        username: string
    }
}) {
    const {isMobile} = useSidebar()

    const fetcher = useFetcher();

    const formRef = useRef<HTMLFormElement>(null);

    return (
        <SidebarMenu>
            <SidebarMenuItem>
                <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <SidebarMenuButton
                            size="lg"
                            className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
                        >
                            <Avatar className="h-8 w-8 rounded-lg grayscale">
                                <AvatarFallback className="rounded-lg">{getInitials(user.username)}</AvatarFallback>
                            </Avatar>
                            <div className="grid flex-1 text-left text-sm leading-tight">
                                <span className="truncate font-medium">{user.username}</span>
                            </div>
                            <EllipsisVerticalIcon className="ml-auto size-4"/>
                        </SidebarMenuButton>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent
                        className="w-(--radix-dropdown-menu-trigger-width) min-w-56 rounded-lg"
                        side={isMobile ? "bottom" : "right"}
                        align="end"
                        sideOffset={4}
                    >
                        <DropdownMenuLabel className="p-0 font-normal">
                            <div className="flex items-center gap-2 px-1 py-1.5 text-left text-sm">
                                <Avatar className="h-8 w-8 rounded-lg">
                                    <AvatarFallback className="rounded-lg">{getInitials(user.username)}</AvatarFallback>
                                </Avatar>
                                <div className="grid flex-1 text-left text-sm leading-tight">
                                    <span className="truncate font-medium">{user.username}</span>
                                </div>
                            </div>
                        </DropdownMenuLabel>
                        <DropdownMenuSeparator/>
                        <fetcher.Form action="/api/logout" method="post" ref={formRef}>
                            <DropdownMenuItem onClick={() => formRef.current?.requestSubmit()}>
                                <LogOutIcon/>Log out
                            </DropdownMenuItem>
                        </fetcher.Form>
                    </DropdownMenuContent>
                </DropdownMenu>
            </SidebarMenuItem>
        </SidebarMenu>
    )
}
