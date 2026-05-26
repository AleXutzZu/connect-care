import React from "react";
import type {Route} from "./+types/layout";
import {protectRouteMiddleware, userContext} from "~/lib/auth";
import {SidebarInset, SidebarProvider} from "~/components/ui/sidebar";
import {AppSidebar} from "~/components/dashboard/app-sidebar";
import {SiteHeader} from "~/components/dashboard/site-header";
import {Outlet} from "react-router";
import {TooltipProvider} from "~/components/ui/tooltip";
import {Toaster} from "~/components/ui/sonner";

export const middleware: Route.MiddlewareFunction[] = [protectRouteMiddleware];

export async function loader({context}: Route.LoaderArgs) {
    const user = context.get(userContext);

    return user!!;
}

export default function DashboardLayout({loaderData}: Route.ComponentProps) {
    return (
        <TooltipProvider>
            <Toaster/>
            <SidebarProvider
                style={
                    {
                        "--sidebar-width": "calc(var(--spacing) * 72)",
                        "--header-height": "calc(var(--spacing) * 12)",
                    } as React.CSSProperties
                }
            >
                <AppSidebar variant="inset" user={{username: loaderData.sub}}/>
                <SidebarInset>
                    <SiteHeader/>
                    <Outlet/>
                </SidebarInset>
            </SidebarProvider>
        </TooltipProvider>
    );
}