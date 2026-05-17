import {LoginForm} from "~/components/login/login-form"
import type {Route} from "./+types/login";
import {authMiddleware, userContext, userToken} from "~/lib/auth";
import {redirect} from "react-router";

export async function loader({context}: Route.LoaderArgs) {
    const user = context.get(userContext);
    if (user) throw redirect("/dashboard");
    return null;
}

export async function action({request}: Route.ActionArgs) {
    const formData = await request.formData();

    const username = formData.get("username") as string;
    const password = formData.get("password") as string;

    const response = await fetch(`${process.env.BASE_URL}/api/auth/login`, {
        method: "POST",
        body: JSON.stringify({username, password}),
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (!response.ok) {
        return {error: "Invalid credentials"};
    }

    const token = await response.text();
    const cookie = await userToken.serialize(token);

    const searchParams = new URL(request.url).searchParams;
    const redirectTo = searchParams.get("redirect") ?? "/dashboard";

    return redirect(redirectTo, {
        headers: {
            "Set-Cookie": cookie
        }
    });
}

export const middleware: Route.MiddlewareFunction[] = [authMiddleware]

export default function Page() {
    return (
        <div className="flex min-h-svh w-full items-center justify-center p-6 md:p-10">
            <title>Teledon | Login</title>
            <div className="w-full max-w-sm">
                <LoginForm/>
            </div>
        </div>
    )
}
