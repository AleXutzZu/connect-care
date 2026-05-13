import type {CellContext} from "@tanstack/react-table";
import {Controller, useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {useFetcher} from "react-router";
import * as React from "react";
import {useCallback, useEffect, useRef} from "react";
import {toast} from "sonner";
import {Field, FieldLabel} from "~/components/ui/field";
import {Input} from "~/components/ui/input";
import {columnSchema} from "~/components/dashboard/table/charity-data-table";
import * as z from "zod";

export function TargetUpdateCell({row}: CellContext<z.infer<typeof columnSchema>, any>) {
    const formSchema = z.object({
        target: z.number().positive()
    });
    type UpdateForm = z.infer<typeof formSchema>;

    const form = useForm<UpdateForm>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            target: row.original.target
        },
        mode: "onChange"
    });

    const fetcher = useFetcher({key: `update-charity-${row.original.id}`});

    const toastId = useRef<string | number | null>(null);

    const formSubmit = useCallback((values: UpdateForm) => {
        toastId.current = toast.loading("Saving changes...");

        fetcher.submit({
            intent: "UPDATE",
            name: row.original.name,
            target: values.target
        }, {action: `/api/charities/${row.original.id}`, method: "post"})
    }, [fetcher, row.original.name, row.original.id]);

    useEffect(() => {
        if (fetcher.state === "idle" && fetcher.data && toastId.current) {

            if (!fetcher.data.ok) {
                toast.error(`Could not save changes`, {id: toastId.current});
            } else {
                toast.success("Saved successfully!", {id: toastId.current});
            }
            toastId.current = null;
        }
    }, [fetcher.state, fetcher.data]);

    return (
        <form onSubmit={form.handleSubmit(formSubmit)}>
            <Controller control={form.control} name="target"
                        render={({field, fieldState}) => (
                            <Field data-invalid={fieldState.invalid} className="w-full flex justify-end">
                                <FieldLabel htmlFor={`${row.original.id}-target`}
                                            className="sr-only">Target</FieldLabel>
                                <div className="flex justify-end items-center gap-1">
                                    <span className="text-foreground">$</span>
                                    <Input {...field} aria-invalid={fieldState.invalid} autoComplete="off"
                                           onChange={(e) => {
                                               const value = e.target.value;
                                               if (value === "") field.onChange("");
                                               else field.onChange(isNaN(Number(value)) ? value : Number(value));
                                           }}
                                           id={`${row.original.id}-target`}
                                           className="h-8 w-20 border-transparent bg-transparent text-right shadow-none hover:bg-input/30 focus-visible:border focus-visible:bg-background dark:bg-transparent dark:hover:bg-input/30 dark:focus-visible:bg-input/30"/>
                                </div>
                            </Field>
                        )}/>
        </form>
    )
}