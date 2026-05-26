import type {Route} from "./+types/api.event-stream";
import {getToken, protectResourceMiddleware} from "~/lib/auth";

export const middleware: Route.MiddlewareFunction[] = [protectResourceMiddleware];

export async function loader({ request }: Route.LoaderArgs) {
    const token = await getToken(request.headers.get("Cookie"));

    try {
        const springResponse = await fetch(`${process.env.BASE_URL}/api/event-stream`, {
            headers: {
                "Accept": "text/event-stream",
                "Authorization": `Bearer ${token}`
            }
        });

        if (!springResponse.ok || !springResponse.body) {
            throw new Response("Upstream stream failed", { status: springResponse.status });
        }

        const stream = new ReadableStream({
            async start(controller) {
                const reader = springResponse.body!.getReader();

                request.signal.addEventListener("abort", () => {
                    reader.cancel();
                    controller.close();
                });

                try {
                    while (true) {
                        const { done, value } = await reader.read();
                        if (done) break;
                        controller.enqueue(value);
                    }
                    controller.close();
                } catch (error) {
                    console.error("Stream reading error:", error);
                    controller.error(error);
                }
            }
        });

        return new Response(stream, {
            headers: {
                "Content-Type": "text/event-stream",
                "Cache-Control": "no-cache, no-transform",
                "Connection": "keep-alive",
                "Content-Encoding": "none"
            },
        });

    } catch (error) {
        console.error("SSE Proxy Failed:", error);
        return new Response("Server Unavailable", { status: 502 });
    }
}