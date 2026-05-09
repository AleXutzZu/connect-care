import type {Route} from "./+types/home";
import {getToken} from "~/lib/auth";

export async function loader({request}: Route.LoaderArgs) {
    const token = await getToken(request.headers.get("Cookie"));

    const response = await fetch(`${process.env.BASE_URL}/api/charities`, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${token}`
        }
    });

    if (!response.ok) {
        console.log(await response.text());
        return [];
    }

    const data = await response.json();

    return data;
}

export default function DashboardIndex() {
    return <>This is the dashboard!!!!</>
}