import type {CellContext} from "@tanstack/react-table";
import {useFetcher} from "react-router";
import * as React from "react";
import {useCallback, useEffect, useRef} from "react";
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
import {DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger} from "~/components/ui/dropdown-menu";
import {Button} from "~/components/ui/button";
import {EllipsisVerticalIcon, Trash2Icon} from "lucide-react";
import {columnSchema} from "~/components/dashboard/table/charity-data-table";
import * as z from "zod";


export function ActionsCell({row}: CellContext<z.infer<typeof columnSchema>, any>) {
    const fetcher = useFetcher({key: `delete-action-${row.original.id}`});
    const toastId = useRef<string | number | null>(null);

    const handleDeletion = useCallback(() => {
        toastId.current = toast.loading("Deleting charity...");
        fetcher.submit({intent: "DELETE"}, {action: `/api/charities/${row.original.id}`, method: "post"});
    }, [fetcher, row.original.id]);

    useEffect(() => {
        if (fetcher.state === "idle" && fetcher.data && toastId.current) {

            if (!fetcher.data.ok) {
                toast.error(`Could not delete charity`, {id: toastId.current});
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
                        <AlertDialogTitle>Delete charity?</AlertDialogTitle>
                        <AlertDialogDescription>
                            This will permanently delete this charity.
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