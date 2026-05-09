import React from "react";
import type {Route} from "./+types/layout";
import {protectRouteMiddleware} from "~/lib/auth";

export const middleware: Route.MiddlewareFunction[] = [protectRouteMiddleware];

export default function DashboardLayout({children}: { children: React.ReactNode }) {
    return (
        <>
            <nav>
                navigation
            </nav>
            <main>
                {children}
            </main>
            <footer>
                header
            </footer>
        </>
    );
}