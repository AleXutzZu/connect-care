import type {Route} from "./+types/api.charity";
import {getToken, protectRouteMiddleware} from "~/lib/auth";
import {format, formatISO, subMonths} from "date-fns";

export const middleware: Route.MiddlewareFunction[] = [protectRouteMiddleware];

export async function action({request, params: {charityId}}: Route.ActionArgs) {
    const formData = await request.formData();

    const intent = formData.get("intent") as "DELETE" | "UPDATE";

    const token = await getToken(request.headers.get("Cookie"));

    if (intent === "UPDATE") {
        const name = formData.get("name") as string;
        const target = Number(formData.get("target"));

        const response = await fetch(`${process.env.BASE_URL}/api/charities/${charityId}`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json",
            },
            body: JSON.stringify({name, target})
        });

        if (response.ok) return {ok: true};
        return {ok: false, message: "failed to update"};
    } else if (intent === "DELETE") {
        const response = await fetch(`${process.env.BASE_URL}/api/charities/${charityId}`, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (response.ok) return {ok: true};
        return {ok: false, message: "failed to delete"};
    }
}

export interface CharityStatistics {
    month: Date,
    totalAmount: number,
    donorCount: number
}

export async function loader({request, params: {charityId}}: Route.LoaderArgs) {
    const token = await getToken(request.headers.get("Cookie"));

    const response = await fetch(`${process.env.BASE_URL}/api/statistics/charities/${charityId}?since=${formatISO(subMonths(Date.now(), 6), {representation: "date"})}`, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${token}`
        }
    });

    if (response.ok) {
        return await response.json() as CharityStatistics[];
    }
    return {message: "Could not retrieve data for this charity"};
}