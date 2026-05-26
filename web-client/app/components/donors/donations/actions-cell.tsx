import type {CellContext} from "@tanstack/react-table";
import type {Donation} from "~/routes/api/api.donor";
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
import {DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger} from "~/components/ui/dropdown-menu";
import {Button} from "~/components/ui/button";
import {EllipsisVerticalIcon, Trash2Icon} from "lucide-react";
import * as React from "react";
import {useFetcher} from "react-router";
import {useCallback, useEffect, useRef} from "react";
import {toast} from "sonner";

export function ActionsCell({row}: CellContext<Donation, any>) {
    const fetcher = useFetcher({key: `delete-donation-action-${row.original.id}`});
    const toastId = useRef<string | number | null>(null);

    const handleDeletion = useCallback(() => {
        toastId.current = toast.loading("Deleting donation...");
        fetcher.submit({intent: "DELETE"}, {action: `/api/donations/${row.original.id}`, method: "post"});
    }, [fetcher, row.original.id]);

    useEffect(() => {
        if (fetcher.state === "idle" && fetcher.data && toastId.current) {

            if (!fetcher.data.ok) {
                toast.error(`Could not delete donation`, {id: toastId.current});
            } else {
                toast.success("Deleted successfully!", {id: toastId.current});
            }
            toastId.current = null;
        }
    }, [fetcher.state, fetcher.data]);

    return (
        <div className="flex justify-end">
            <AlertDialog>
                <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <Button
                            variant="ghost"
                            className="flex size-8 text-muted-foreground data-[state=open]:bg-muted"
                            size="icon">
                            <EllipsisVerticalIcon/>
                            <span className="sr-only">Open menu</span>
                        </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end" className="w-32">
                        <AlertDialogTrigger asChild>
                            <DropdownMenuItem variant="destructive">Delete</DropdownMenuItem>
                        </AlertDialogTrigger>
                    </DropdownMenuContent>
                </DropdownMenu>

                <AlertDialogContent size="sm">
                    <AlertDialogHeader>
                        <AlertDialogMedia
                            className="bg-destructive/10 text-destructive dark:bg-destructive/20 dark:text-destructive">
                            <Trash2Icon/>
                        </AlertDialogMedia>
                        <AlertDialogTitle>Delete donation?</AlertDialogTitle>
                        <AlertDialogDescription>
                            This will permanently delete this donation.
                        </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                        <AlertDialogCancel variant="outline">Cancel</AlertDialogCancel>
                        <AlertDialogAction variant="destructive" onClick={handleDeletion}>Delete</AlertDialogAction>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>
        </div>
    );
}