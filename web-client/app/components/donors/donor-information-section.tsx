import type {DonorWithoutDonations} from "~/routes/api/api.donors";
import * as React from "react";
import {useCallback, useEffect, useRef} from "react";
import {Controller, useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {useFetcher} from "react-router";
import {toast} from "sonner";
import {Field, FieldError, FieldGroup, FieldLabel} from "~/components/ui/field";
import {Input} from "~/components/ui/input";
import {z} from "zod";

export function DonorInformationContent(props: { donor: DonorWithoutDonations }) {
    return (
        <>
            <div className="grid grid-cols-2 gap-4">

                <div className="grid gap-2">
                    <h3 className="font-medium">First Name</h3>
                    <p>{props.donor.firstName}</p>
                </div>

                <div className="grid gap-2">
                    <h3 className="font-medium">Last Name</h3>
                    <p>{props.donor.lastName}</p>
                </div>

            </div>

            <div className="grid gap-2">
                <h3 className="font-medium">Address</h3>
                <p>{props.donor.address}</p>
            </div>

            <div className="grid gap-2">
                <h3 className="font-medium">Phone Number</h3>
                <p>{props.donor.phoneNumber}</p>
            </div>
        </>
    );
}

export function DonorEditInformationContent(props: { donor: DonorWithoutDonations, cancelEdit: () => void }) {
    const formSchema = z.object({
        firstName: z.string()
            .min(1, "First name cannot be empty")
            .max(50, "First name is too long"),
        lastName: z.string()
            .min(1, "Last name cannot be empty")
            .max(50, "Last name is too long"),
        address: z.string()
            .min(1, "Address cannot be empty")
            .max(100, "Address is too long"),
        phoneNumber: z.string()
            .regex(/\d{10}/, "Invalid phone number")
    });

    type DonorEditFormSchema = z.infer<typeof formSchema>;

    const form = useForm<DonorEditFormSchema>({
        resolver: zodResolver(formSchema),
        defaultValues: {...props.donor},
        mode: "onChange"
    });

    const toastId = useRef<string | number | null>(null);

    const fetcher = useFetcher({key: `donor-edit-${props.donor.id}`});

    const formSubmit = useCallback((values: DonorEditFormSchema) => {
        toastId.current = toast.loading("Updating information...");
        fetcher.submit({...values, intent: "UPDATE"}, {
            action: `/api/donors/${props.donor.id}`, method: "post"
        });
    }, [fetcher, props.donor.id]);

    useEffect(() => {
        if (fetcher.state === "idle" && fetcher.data && toastId.current) {

            if (!fetcher.data.ok) {
                toast.error(`Could not update donor`, {id: toastId.current});
            } else {
                toast.success("Updated successfully!", {id: toastId.current});
                props.cancelEdit();
            }

            toastId.current = null;
        }
    }, [fetcher.state, fetcher.data, props]);

    return (
        <form id={`edit-donor-${props.donor.id}`} onSubmit={form.handleSubmit(formSubmit)}>
            <FieldGroup>
                <div className="grid grid-cols-2 gap-4">

                    <Controller control={form.control} name="firstName" render={({field, fieldState}) => (
                        <Field data-invalid={fieldState.invalid}>
                            <FieldLabel htmlFor="firstName">First Name</FieldLabel>
                            <Input {...field} aria-invalid={fieldState.invalid} id="firstName"/>
                            {fieldState.invalid && (<FieldError errors={[fieldState.error]}/>)}
                        </Field>
                    )}/>

                    <Controller control={form.control} name="lastName" render={({field, fieldState}) => (
                        <Field data-invalid={fieldState.invalid}>
                            <FieldLabel htmlFor="lastName">Last Name</FieldLabel>
                            <Input {...field} aria-invalid={fieldState.invalid} id="lastName"/>
                            {fieldState.invalid && (<FieldError errors={[fieldState.error]}/>)}
                        </Field>
                    )}/>
                </div>

                <Controller control={form.control} name="address" render={({field, fieldState}) => (
                    <Field data-invalid={fieldState.invalid}>
                        <FieldLabel htmlFor="address">Address</FieldLabel>
                        <Input {...field} aria-invalid={fieldState.invalid} id="address"/>
                        {fieldState.invalid && (<FieldError errors={[fieldState.error]}/>)}
                    </Field>
                )}/>

                <Controller control={form.control} name="phoneNumber" render={({field, fieldState}) => (
                    <Field data-invalid={fieldState.invalid}>
                        <FieldLabel htmlFor="phoneNumber">Phone Number</FieldLabel>
                        <Input {...field} aria-invalid={fieldState.invalid} id="phoneNumber"/>
                        {fieldState.invalid && (<FieldError errors={[fieldState.error]}/>)}
                    </Field>
                )}/>
            </FieldGroup>
        </form>
    );
}