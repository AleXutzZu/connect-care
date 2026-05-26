import type {Route} from "./+types/api.donation";
import {getToken, protectResourceMiddleware} from "~/lib/auth";

export const middleware: Route.MiddlewareFunction[] = [protectResourceMiddleware];

export async function action({request, params: {donationId}}: Route.ActionArgs) {
    const formData = await request.formData();

    const intent = formData.get("intent") as "DELETE" | "UPDATE";

    const token = await getToken(request.headers.get("Cookie"));

    if (intent === "DELETE") {
        const response = await fetch(`${process.env.BASE_URL}/api/donations/${donationId}`, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (response.ok) return {ok: true};
        return {ok: false, message: "failed to delete"};
    }

    return {ok: false, message: "cannot process"};
}