import type {Route} from "./+types/home";
import {redirect} from "react-router";

export function meta({}: Route.MetaArgs) {
    return [
        {title: "Teledon Management"},
        {name: "description", content: "Seamlessly manage donations for charitable causes."},
    ];
}

export async function loader() {
    return redirect("/dashboard");
}

export default function Home() {
    return <></>
}
