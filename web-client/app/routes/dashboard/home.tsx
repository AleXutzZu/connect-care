import type {Route} from "./+types/home";
import {getToken} from "~/lib/auth";
import {SectionCards} from "~/components/dashboard/section-cards";
import {ChartAreaInteractive} from "~/components/dashboard/chart-area-interactive";
import {CharityDataTable} from "~/components/dashboard/table/charity-data-table";
import {formatISO, subMonths} from "date-fns";

export interface DonationStatistics {
    totalAmount: number,
    date: Date,
    donationCount: number
}

export interface MonthlyActiveDonorStatistics {
    current: number,
    previous: number,
}

export interface DonorStatistics {
    current: number,
    previous: number,
}

export interface Statistics {
    dailyDonations: DonationStatistics[],
    monthlyActiveDonors: MonthlyActiveDonorStatistics,
    monthToDateDonors: DonorStatistics
}

export async function loader({request}: Route.LoaderArgs) {
    const token = await getToken(request.headers.get("Cookie"));

    const response = await fetch(`${process.env.BASE_URL}/api/statistics?since=${formatISO(subMonths(Date.now(), 3), {representation: "date"})}`, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${token}`
        }
    });

    return await response.json() as Statistics;
}

export default function DashboardIndex({loaderData}: Route.ComponentProps) {
    return (
        <div className="flex flex-1 flex-col">
            <div className="@container/main flex flex-1 flex-col gap-2">
                <div className="flex flex-col gap-4 py-4 md:gap-6 md:py-6">
                    <SectionCards {...loaderData}/>
                    <div className="px-4 lg:px-6">
                        <ChartAreaInteractive data={loaderData.dailyDonations}/>
                    </div>
                    <CharityDataTable/>
                </div>
            </div>
        </div>
    )
}