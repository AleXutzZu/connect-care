import type {Route} from "./+types/api.donor";
import {getToken, protectResourceMiddleware} from "~/lib/auth";

export const middleware: Route.MiddlewareFunction[] = [protectResourceMiddleware];

export interface DonorStatistics {
    totalDonations: number,
    averageDonation?: number,
    highestDonation?: {
        charityName: string,
        amount: number
    }
    lastDonation?: Date
}

export async function action({request, params: {donorId}}: Route.ActionArgs) {
    const formData = await request.formData();

    const intent = formData.get("intent") as "DELETE" | "UPDATE";

    const token = await getToken(request.headers.get("Cookie"));

    if (intent === "UPDATE") {
        const firstName = formData.get("firstName") as string;
        const lastName = formData.get("lastName") as string;
        const address = formData.get("address") as string;
        const phoneNumber = formData.get("phoneNumber") as string;

        const response = await fetch(`${process.env.BASE_URL}/api/donors/${donorId}`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json",
            },
            body: JSON.stringify({firstName, lastName, address, phoneNumber})
        });

        if (response.ok) return {ok: true};
        return {ok: false, message: "failed to update"};
    } else if (intent === "DELETE") {
        const response = await fetch(`${process.env.BASE_URL}/api/donors/${donorId}`, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (response.ok) return {ok: true};
        return {ok: false, message: "failed to delete"};
    }
}

export async function loader({request, params: {donorId}}: Route.LoaderArgs) {
    const token = await getToken(request.headers.get("Cookie"));

    const response = await fetch(`${process.env.BASE_URL}/api/statistics/donors/${donorId}`, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${token}`
        }
    });

    if (response.ok) {
        let newVar = await response.json();
        return newVar as DonorStatistics;
    }

    return {message: "Could not retrieve data for this donor"};
}