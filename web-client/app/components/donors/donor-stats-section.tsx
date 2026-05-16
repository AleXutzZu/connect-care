import type {Donor} from "~/routes/api/api.donors";
import {useFetcher} from "react-router";
import * as React from "react";
import {useEffect} from "react";
import {format} from "date-fns";
import type {DonorStatistics} from "~/routes/api/api.statistics.donor";

export function DonorStatsSection(props: { donor: Donor }) {
    const fetcher = useFetcher({key: `donor-stats-${props.donor.id}`});
    const isLoading = fetcher.state !== "idle" && !fetcher.data;

    useEffect(() => {
        if (!fetcher.data && fetcher.state === "idle") fetcher.load(`/api/statistics/donors/${props.donor.id}`);
    }, [props.donor.id, fetcher]);

    const data = fetcher.data ? (fetcher.data as DonorStatistics) : null;

    const currencyFormatter = Intl.NumberFormat("en-US", {
        style: "currency",
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
        currency: "USD"
    })

    return (
        <div className="grid grid-cols-2 gap-x-4 gap-y-6">
            <div className="grid gap-2">
                <h3 className="font-medium">Registered on</h3>
                <p>{format(props.donor.createdOn, "MMMM d, yyyy")}</p>
            </div>

            <div className="grid gap-2">
                <h3 className="font-medium">Donations</h3>
                {isLoading && <div className="animate-pulse w-20 h-4 bg-muted rounded-md"/>}
                {!isLoading && <p>{data?.totalDonations}</p>}
            </div>

            <div className="grid gap-2">
                <h3 className="font-medium">Average donation sum</h3>
                {isLoading && <div className="animate-pulse w-20 h-4 bg-muted rounded-md"/>}
                {!isLoading && <p>{data?.averageDonation ? currencyFormatter.format(data?.averageDonation) : "N/A"}</p>}
            </div>

            <div className="grid gap-2">
                <h3 className="font-medium">Highest donation</h3>
                {isLoading && <div className="animate-pulse w-20 h-4 bg-muted rounded-md"/>}
                {!isLoading &&
                    <p>{data?.highestDonation ? `${currencyFormatter.format(data?.highestDonation.amount)} @ ${data?.highestDonation.charityName}` : "N/A"}</p>}
            </div>

            <div className="grid gap-2">
                <h3 className="font-medium">Last donation</h3>
                {isLoading && <div className="animate-pulse w-20 h-4 bg-muted rounded-md"/>}
                {!isLoading && <p>{data?.lastDonation ? format(data?.lastDonation, "MMMM d, yyyy @ HH:mm") : "N/A"}</p>}
            </div>
        </div>
    );
}