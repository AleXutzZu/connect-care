import type {Route} from "./+types/api.donors";
import {getToken, protectResourceMiddleware} from "~/lib/auth";

export interface DonorWithoutDonations {
    id: number;
    firstName: string;
    lastName: string;
    address: string;
    phoneNumber: string;
    createdOn: Date;
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

    return data as Page<DonorWithoutDonations>;
}

export async function action({request}: Route.ActionArgs) {
    const formData = await request.formData();

    const firstName = formData.get("firstName") as string;
    const lastName = formData.get("lastName") as string;
    const address = formData.get("address") as string;
    const phoneNumber = formData.get("phoneNumber") as string;

    const token = await getToken(request.headers.get("Cookie"));

    const response = await fetch(`${process.env.BASE_URL}/api/donors`, {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json",
        },
        body: JSON.stringify({firstName, lastName, address, phoneNumber})
    });

    if (response.ok) {
        const donor = await response.json();
        return {ok: true, data: donor as DonorWithoutDonations};
    }
    return {ok: false, message: "could not create donor"};
}