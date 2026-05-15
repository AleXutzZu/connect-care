import {type Route} from "./+types/api.donors";
import {getToken, protectResourceMiddleware} from "~/lib/auth";

export interface Donor {
    id: number;
    firstName: string;
    lastName: string;
    address: string;
    phoneNumber: string;
    createdOn: string;
}

export interface Page<T> {
    content: T[],
    totalPages: number,
    totalElements: number,
}

export const middleware: Route.MiddlewareFunction[] = [protectResourceMiddleware];

export async function loader({request}: Route.LoaderArgs) {
    const searchParams = new URL(request.url).searchParams;

    const token = await getToken(request.headers.get("Cookie"));

    const response = await fetch(`${process.env.BASE_URL}/api/donors?${searchParams.toString()}`, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${token}`
        }
    });

    const data = await response.json();

    return data as Page<Donor>;
}
