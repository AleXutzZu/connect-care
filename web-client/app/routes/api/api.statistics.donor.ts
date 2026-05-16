import type {Route} from "./+types/api.statistics.donor";
import {getToken, protectResourceMiddleware} from "~/lib/auth";

export const middleware: Route.MiddlewareFunction[] = [protectResourceMiddleware];

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

export interface DonorStatistics {
    totalDonations: number,
    averageDonation?: number,
    highestDonation?: {
        charityName: string,
        amount: number
    }
    lastDonation?: Date
}