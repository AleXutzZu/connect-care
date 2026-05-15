import type {Donor} from "~/routes/api/api.donors";
import {Drawer, DrawerContent, DrawerDescription, DrawerHeader, DrawerTitle} from "~/components/ui/drawer";
import * as React from "react";
import {useState} from "react";
import {DonorCard} from "~/components/donors/donor-card";


export function DonorDrawer(props: { donor: Donor, open: boolean, onOpenChange: (state: boolean) => void }) {

    const [blockInteraction, setBlockInteraction] = useState(false);


    return (
        <Drawer direction="bottom" open={props.open} onOpenChange={props.onOpenChange}>

            <DrawerContent className="max-h-full" onInteractOutside={event => {
                if (blockInteraction) event.preventDefault()
            }} onEscapeKeyDown={event => {
                if (blockInteraction) event.preventDefault()
            }}>
                <div className="overflow-y-auto">
                    <DrawerHeader className="gap-1">
                        <DrawerTitle>{props.donor.firstName} {props.donor.lastName}</DrawerTitle>
                        <DrawerDescription>View information about this donor</DrawerDescription>
                    </DrawerHeader>
                    <DonorCard donor={props.donor} setBlockBackground={setBlockInteraction}/>
                </div>
            </DrawerContent>
        </Drawer>
    );
}