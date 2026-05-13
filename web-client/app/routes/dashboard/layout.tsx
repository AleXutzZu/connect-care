import React from "react";
import type {Route} from "./+types/layout";
import {protectRouteMiddleware} from "~/lib/auth";
import {SidebarInset, SidebarProvider} from "~/components/ui/sidebar";
import {AppSidebar} from "~/components/app-sidebar";
import {SiteHeader} from "~/components/site-header";
import {Outlet} from "react-router";
import {TooltipProvider} from "~/components/ui/tooltip";
import {Toaster} from "~/components/ui/sonner";

export const middleware: Route.MiddlewareFunction[] = [protectRouteMiddleware];

export default function DashboardLayout() {
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
                <AppSidebar variant="inset"/>
                <SidebarInset>
                    <SiteHeader/>
                    <Outlet/>
                </SidebarInset>
            </SidebarProvider>
        </TooltipProvider>
    );
}