import type{Route} from "./+types/api.logout";
import {protectResourceMiddleware, userContext, userToken} from "~/lib/auth";
import {redirect} from "react-router";

export const middleware: Route.MiddlewareFunction[] = [protectResourceMiddleware];

export async function action({context}: Route.ActionArgs) {
    context.set(userContext, null);
    const emptyCookie = await userToken.serialize('', {maxAge: 0});
    return redirect("/login", {
        headers: {
            "Set-Cookie": emptyCookie,
        }
    })
}