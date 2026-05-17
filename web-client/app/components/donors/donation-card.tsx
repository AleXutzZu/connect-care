import type {DonorWithoutDonations} from "~/routes/api/api.donors";
import {Card, CardAction, CardContent, CardDescription, CardHeader, CardTitle} from "~/components/ui/card";
import {Button} from "~/components/ui/button";
import * as React from "react";
import {DonationDataTable} from "~/components/donors/donations/donation-data-table";
import {CreateDonationForm} from "~/components/donors/donations/create-donation-form";
import {useIsMobile} from "~/hooks/use-mobile";


export function DonationCard(props: { donor: DonorWithoutDonations }) {
    const isMobile = useIsMobile();
    return (
        <Card className={`${isMobile ? "ring-0" : ""}`}>
            <CardHeader>
                <CardTitle>Donation History</CardTitle>

                <CardDescription>
                    View information on past donations
                </CardDescription>

                <CardAction>
                    <CreateDonationForm actionButton={<Button variant="link">Add donation</Button>}
                                        donor={props.donor}/>
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