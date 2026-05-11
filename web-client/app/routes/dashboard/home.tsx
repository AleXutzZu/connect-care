import type {Route} from "./+types/home";
import {getToken} from "~/lib/auth";
import {SectionCards} from "~/components/section-cards";
import {ChartAreaInteractive} from "~/components/chart-area-interactive";
import {DataTable} from "~/components/data-table";
import data from "~/dashboard/data.json";

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

    return await response.json();
}

export default function DashboardIndex() {
    return (
        <div className="flex flex-1 flex-col">
            <div className="@container/main flex flex-1 flex-col gap-2">
                <div className="flex flex-col gap-4 py-4 md:gap-6 md:py-6">
                    <SectionCards/>
                    <div className="px-4 lg:px-6">
                        <ChartAreaInteractive/>
                    </div>
                    <DataTable data={data}/>
                </div>
            </div>
        </div>
    )
}