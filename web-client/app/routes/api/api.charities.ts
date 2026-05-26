import type {Route} from "./+types/api.charities";
import {getToken, protectResourceMiddleware} from "~/lib/auth";

export interface Charity {
    id: number;
    name: string;
    raisedAmount: number;
    target: number;
    registeredBy: string
}

export interface Page<T> {
    content: T[],
    totalPages: number,
    totalElements: number,
}

export const middleware: Route.MiddlewareFunction[] = [protectResourceMiddleware];

export async function loader({request}: Route.LoaderArgs) {
    const searchParams = new URL(request.url).searchParams;

    const page = Number(searchParams.get("page")) || 0;
    const size = Number(searchParams.get("size")) || 10;

    const token = await getToken(request.headers.get("Cookie"));

    const response = await fetch(`${process.env.BASE_URL}/api/charities?${searchParams.toString()}`, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${token}`
        }
    });

    const data = await response.json();

    return data as Page<Charity>;
}

export async function action({request}: Route.ActionArgs) {
    const formData = await request.formData();

    const name = formData.get("name") as string;
    const target = Number(formData.get("target"));

    const token = await getToken(request.headers.get("Cookie"));

    const response = await fetch(`${process.env.BASE_URL}/api/charities`, {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json",
        },
        body: JSON.stringify({name, target})
    });

    if (response.ok) return {ok: true};
    return {ok: false, message: "could not create charity"};
}