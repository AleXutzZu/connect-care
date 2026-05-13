import {useIsMobile} from "~/hooks/use-mobile";
import * as React from "react";
import {Suspense, useCallback, useEffect, useMemo, useRef} from "react";
import {
    DrawerClose,
    DrawerContent,
    DrawerDescription,
    DrawerFooter,
    DrawerHeader,
    DrawerTitle
} from "~/components/ui/drawer";
import {Button} from "~/components/ui/button";
import {toast} from "sonner";
import {Label} from "~/components/ui/label";
import {Input} from "~/components/ui/input";
import {columnSchema} from "~/components/dashboard/table/charity-data-table";
import * as z from "zod";
import {Controller, useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {useFetcher, useFetchers} from "react-router";
import {Field, FieldError, FieldLabel} from "~/components/ui/field";
import type {CharityStatistics} from "~/routes/api/api.charity";
import {format} from "date-fns";

const ChartArea = React.lazy(() =>
    import("./chart-area").then(mod => ({default: mod.ChartArea}))
);


function UpdateCharityForm(props: {
    id: number,
    defaultName: string,
    defaultTarget: number,
    registeredBy: string,
    modalHandler: (state: boolean) => void
}) {
    const formSchema = z.object({
        name: z.string()
            .min(3, "Charity name must be at least 3 characters")
            .max(50, "Charity name must be at most 50 characters"),
        target: z.number("Target must be a number").positive("Target must be a positive number")
    });

    type FormValues = z.infer<typeof formSchema>;

    const form = useForm<FormValues>(
        {
            resolver: zodResolver(formSchema),
            defaultValues: {
                name: props.defaultName,
                target: props.defaultTarget
            },
            mode: "onChange"
        }
    );

    const fetcher = useFetcher({key: `update-charity-form-${props.id}`});
    const toastId = useRef<string | number | null>(null);

    const formSubmit = useCallback((values: FormValues) => {
        toastId.current = toast.loading("Updating information...");

        fetcher.submit({name: values.name, target: values.target, intent: "UPDATE"}, {
            action: `/api/charities/${props.id}`, method: "post"
        });
    }, [fetcher, props.id]);

    useEffect(() => {
        if (fetcher.state === "idle" && fetcher.data && toastId.current) {

            if (!fetcher.data.ok) {
                toast.error(`Could not update charity`, {id: toastId.current});
            } else {
                toast.success("Updated successfully!", {id: toastId.current});
                props.modalHandler(false);
            }

            toastId.current = null;
        }
    }, [fetcher.state, fetcher.data, props]);

    return (
        <form
            id={`update-form-${props.id}`}
            className="flex flex-col gap-4"
            onSubmit={form.handleSubmit(formSubmit)}
        >
            <Controller control={form.control} name="name" render={({field, fieldState}) => (
                <Field data-invalid={fieldState.invalid}>
                    <FieldLabel htmlFor={`name-update-form-${props.id}`}>Name</FieldLabel>
                    <Input {...field}
                           aria-invalid={fieldState.invalid}
                           id={`name-update-form-${props.id}`}
                    />
                    {fieldState.invalid && (<FieldError errors={[fieldState.error]}/>)}
                </Field>)}/>

            <Controller control={form.control} name="target" render={({field, fieldState}) => (
                <Field data-invalid={fieldState.invalid}>
                    <FieldLabel htmlFor={`target-update-form-${props.id}`}>Target</FieldLabel>
                    <Input {...field}
                           onChange={(e) => {
                               const value = e.target.value;
                               if (value === "") field.onChange("");
                               else field.onChange(isNaN(Number(value)) ? value : Number(value));
                           }}
                           aria-invalid={fieldState.invalid}
                           id={`target-update-form-${props.id}`}
                    />
                    {fieldState.invalid && (<FieldError errors={[fieldState.error]}/>)}
                </Field>)}/>

            <div className="flex flex-col gap-3">
                <Label htmlFor={`registeredBy-${props.id}`}>Registered by</Label>
                <Input readOnly value={props.registeredBy} id={`registeredBy-${props.id}`} disabled/>
            </div>
        </form>
    );
}

export function TableCellViewer({item, drawerHandler}: {
                                    item: z.infer<typeof columnSchema>,
                                    drawerHandler: () => void
                                }
) {
    return (
        <Button variant="link" className="w-fit px-0 text-left text-foreground" onClick={drawerHandler}>
            {item.name}
        </Button>
    );
}

export function CharityDrawerContent({
                                         item,
                                         setIsOpen
                                     }: {
    item: z.infer<typeof columnSchema>,
    setIsOpen: (open: boolean) => void
}) {
    const isMobile = useIsMobile();

    const fetcher = useFetcher({key: `chart-fetcher-${item.id}`});
    const isLoading = fetcher.state !== "idle" && !fetcher.data;

    useEffect(() => {
        if (isMobile) return;
        if (!fetcher.data && fetcher.state === "idle") {
            fetcher.load(`/api/charities/${item.id}`);
        }
    }, [isMobile, fetcher, item.id]);

    const transformedChartData = useMemo(() => {
        if (!fetcher.data) return [];
        const data = fetcher.data as CharityStatistics[];
        return data.map(d => ({
            month: format(new Date(d.month), "MMMM"),
            donorCount: d.donorCount,
            totalAmount: d.totalAmount
        }));
    }, [fetcher.data]);

    const isSubmitting = useFetchers().some(
        (f) => f.key === `update-charity-form-${item.id}` && f.state !== "idle"
    );

    return (
        <DrawerContent
            onEscapeKeyDown={(e) => {
                if (isSubmitting) e.preventDefault()
            }}
            onInteractOutside={(e) => {
                if (isSubmitting) e.preventDefault()
            }}>
            <DrawerHeader className="gap-1">
                <DrawerTitle>{item.name}</DrawerTitle>
                <DrawerDescription>
                    Showing total donations for the last 6 months
                </DrawerDescription>
            </DrawerHeader>

            <div className="flex flex-col gap-4 overflow-y-auto px-4 text-sm">
                {!isMobile && (
                    <>
                        {isLoading ? (
                            <div className="w-full h-50 animate-pulse bg-muted rounded-md"></div>
                        ) : transformedChartData.length > 0 ? (
                            <Suspense fallback={<div className="w-full h-50 animate-pulse bg-muted rounded-md"/>}>
                                <ChartArea data={transformedChartData}/>
                            </Suspense>
                        ) : (
                            <div
                                className="w-full h-50 bg-muted flex items-center justify-center text-muted-foreground rounded-md">
                                No data for this charity
                            </div>
                        )}
                    </>
                )}

                <UpdateCharityForm
                    id={item.id}
                    defaultName={item.name}
                    defaultTarget={item.target}
                    modalHandler={setIsOpen}
                    registeredBy={item.registeredBy}
                />
            </div>

            <DrawerFooter>
                <Button type="submit" form={`update-form-${item.id}`} disabled={isSubmitting}>Submit</Button>
                <DrawerClose asChild>
                    <Button variant="outline" disabled={isSubmitting}>Done</Button>
                </DrawerClose>
            </DrawerFooter>
        </DrawerContent>
    );
}