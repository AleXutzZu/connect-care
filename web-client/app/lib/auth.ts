import {createContext, createCookie, type MiddlewareFunction, redirect} from "react-router";
import {jwtDecode} from "jwt-decode";

export const userToken = createCookie("jwt", {
    httpOnly: true,
    sameSite: "lax",
    secure: process.env.NODE_ENV === "production",
    maxAge: 604800,
});

export const userContext = createContext<UserPayload | null>(null);

interface UserPayload {
    sub: string,
    exp: number,
}

function isTokenExpired(user: UserPayload) {
    return user.exp * 1000 < Date.now();
}

export const authMiddleware: MiddlewareFunction<Response> = async ({request, context}, next) => {
    const cookieHeader = request.headers.get("Cookie");
    const token: string | null = await getToken(cookieHeader);

    if (!token) {
        context.set(userContext, null);
        return next();
    }

    const user = jwtDecode<UserPayload>(token);

    if (isTokenExpired(user)) {
        context.set(userContext, null);
        return next();
    }

    context.set(userContext, user);
    return next();
}

export function getToken(cookieHeader: string | null) {
    return userToken.parse(cookieHeader);
}

export const protectResourceMiddleware: MiddlewareFunction<Response> = async ({request, context}, next) => {
    const user = context.get(userContext);

    if (!user) {
        const emptyCookie = await userToken.serialize('', {maxAge: 0});
        throw redirect(`/login?redirect=${encodeURIComponent("/dashboard")}`, {
            headers: {
                "Set-Cookie": emptyCookie
            }
        });
    }
    return next();
}

export const protectRouteMiddleware: MiddlewareFunction<Response> = async ({request, context}, next) => {
    const user = context.get(userContext);

    if (!user) {
        const redirectUrl = `/login?redirect=${encodeURIComponent(new URL(request.url).pathname)}`

        const emptyCookie = await userToken.serialize('', {maxAge: 0});
        throw redirect(redirectUrl, {
            headers: {
                "Set-Cookie": emptyCookie
            }
        });
    }
    return next();
}