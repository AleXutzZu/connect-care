import {Card, CardAction, CardContent, CardDescription, CardFooter, CardHeader, CardTitle} from "~/components/ui/card";
import {Button} from "~/components/ui/button";
import * as React from "react";
import {useCallback, useEffect, useRef, useState} from "react";
import type {DonorWithoutDonations} from "~/routes/api/api.donors";
import {Separator} from "~/components/ui/separator";
import {useFetcher} from "react-router";
import {toast} from "sonner";
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogMedia,
    AlertDialogTitle,
    AlertDialogTrigger
} from "~/components/ui/alert-dialog";
import {Trash2Icon} from "lucide-react";
import {DonorStatsSection} from "~/components/donors/donor-stats-section";
import {DonorEditInformationContent, DonorInformationContent} from "~/components/donors/donor-information-section";
import {useIsMobile} from "~/hooks/use-mobile";

export function DonorCard(props: { donor: DonorWithoutDonations, setBlockBackground: (state: boolean) => void }) {
    const isMobile = useIsMobile();
    const [editMode, setEditMode] = useState(false);

    const cancelEdit = useCallback(() => {
        setEditMode(false);
        props.setBlockBackground(false);
    }, [props]);

    const enableEdit = useCallback(() => {
        setEditMode(true);
        props.setBlockBackground(true);
    }, [props]);

    return (
        <Card className={`${isMobile ? "ring-0" : ""}`}>
            <CardHeader>
                <CardTitle>Donor Information</CardTitle>

                <CardDescription>
                    View information about this donor
                </CardDescription>

                <CardAction>
                    {!editMode && <Button variant="link" onClick={enableEdit}>Edit</Button>}
                    {editMode && <div className="flex gap-2">
                        <Button type="submit" form={`edit-donor-${props.donor.id}`}>Save</Button>
                        <Button variant="link" onClick={cancelEdit}>Cancel</Button>
                    </div>}
                </CardAction>
            </CardHeader>

            <CardContent>
                <div className="flex flex-col gap-6">
                    {!editMode && <DonorInformationContent donor={props.donor}/>}
                    {editMode && <DonorEditInformationContent donor={props.donor} cancelEdit={cancelEdit}/>}
                    <Separator/>

                    <div className="grid gap-1">
                        <h2 className="font-medium text-base">Statistics</h2>
                        <p className="text-muted-foreground">View some statistics
                            about {props.donor.firstName} {props.donor.lastName}</p>
                    </div>
                    <DonorStatsSection donor={props.donor}/>
                </div>
            </CardContent>
            <CardFooter>
                <DeleteDonorButton id={props.donor.id}/>
            </CardFooter>
        </Card>
    );
}

export function EmptyDonorCard() {
    return (
        <Card>
            <CardHeader>
                <CardTitle>Donor Information</CardTitle>

                <CardAction>
                    <Button variant="link" disabled>Edit</Button>
                </CardAction>
            </CardHeader>
            <CardContent>
                <p className="text-muted-foreground">
                    Select a donor to view or edit their information
                </p>
            </CardContent>
        </Card>
    )
}

function DeleteDonorButton({id}: { id: number }) {
    const fetcher = useFetcher({key: `delete-bonor-btn-${id}`});

    const toastId = useRef<string | number | null>(null);

    const handleDeletion = useCallback(() => {
        toastId.current = toast.loading("Deleting donor...");
        fetcher.submit({intent: "DELETE"}, {action: `/api/donors/${id}`, method: "post"});
    }, [fetcher, id]);

    useEffect(() => {
        if (fetcher.state === "idle" && fetcher.data && toastId.current) {
            if (!fetcher.data.ok) {
                toast.error(`Could not delete donor`, {id: toastId.current});
            } else {
                toast.success("Deleted successfully!", {id: toastId.current});
            }
            toastId.current = null;
        }
    }, [fetcher.state, fetcher.data]);

    return (
        <AlertDialog>
            <AlertDialogTrigger asChild>
                <Button variant="destructive" className="w-full">Delete donor</Button>
            </AlertDialogTrigger>
            <AlertDialogContent size="sm">
                <AlertDialogHeader>
                    <AlertDialogMedia
                        className="bg-destructive/10 text-destructive dark:bg-destructive/20 dark:text-destructive">
                        <Trash2Icon/>
                    </AlertDialogMedia>
                    <AlertDialogTitle>Delete donor?</AlertDialogTitle>
                    <AlertDialogDescription>
                        This will permanently delete all data associated with this donor, including donations.
                        This cannot be undone.
                    </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                    <AlertDialogCancel variant="outline">Cancel</AlertDialogCancel>
                    <AlertDialogAction variant="destructive" onClick={handleDeletion}>Delete</AlertDialogAction>
                </AlertDialogFooter>
            </AlertDialogContent>
        </AlertDialog>
    );
}
