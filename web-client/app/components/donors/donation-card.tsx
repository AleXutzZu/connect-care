import type {DonorWithoutDonations} from "~/routes/api/api.donors";
import {Card, CardAction, CardContent, CardDescription, CardHeader, CardTitle} from "~/components/ui/card";
import {Button} from "~/components/ui/button";
import * as React from "react";
import {DonationDataTable} from "~/components/donors/donations/donation-data-table";


export function DonationCard(props: { donor: DonorWithoutDonations }) {
    return (
        <Card>
            <CardHeader>
                <CardTitle>Donation History</CardTitle>

                <CardDescription>
                    View information on past donations
                </CardDescription>

                <CardAction>
                    <Button variant="link">Add donation</Button>
                </CardAction>
            </CardHeader>

            <CardContent>
                <div className="flex flex-col gap-6">
                    <DonationDataTable donorId={props.donor.id}/>
                </div>
            </CardContent>
        </Card>
    )

}