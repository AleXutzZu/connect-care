import type {Route} from "./+types/api.donations";
import {getToken, protectResourceMiddleware} from "~/lib/auth";

export const middleware: Route.MiddlewareFunction[] = [protectResourceMiddleware];

export async function action({request}: Route.ActionArgs) {
    const formData = await request.formData();

    const donorId = Number(formData.get("donorId"));
    const charityId = Number(formData.get("charityId"));
    const amount = Number(formData.get("amount"));

    const token = await getToken(request.headers.get("Cookie"));

    const response = await fetch(`${process.env.BASE_URL}/api/donations`, {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json",
        },
        body: JSON.stringify({donorId, charityId, amount})
    });

    if (response.ok) return {ok: true};
    return {ok: false, message: "could not create donation"};
}