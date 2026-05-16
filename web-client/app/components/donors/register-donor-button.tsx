import {Button} from "~/components/ui/button";
import {PlusIcon} from "lucide-react";
import * as React from "react";
import {useCallback, useEffect, useRef, useState} from "react";
import {
    Dialog,
    DialogClose,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger
} from "~/components/ui/dialog";
import {Field, FieldError, FieldGroup, FieldLabel} from "~/components/ui/field";
import {Controller, useForm, type UseFormReturn} from "react-hook-form";
import {Input} from "~/components/ui/input";
import {zodResolver} from "@hookform/resolvers/zod";
import {useFetcher} from "react-router";
import {toast} from "sonner";
import {type DonorFormSchema, donorFormSchema} from "~/lib/form-schemas";


export function CreateDonorForm(props: {
    form: UseFormReturn<DonorFormSchema, any, DonorFormSchema>,
    formSubmit: (values: DonorFormSchema) => void,
}) {
    return (
        <FieldGroup>
            <form onSubmit={props.form.handleSubmit(props.formSubmit)} id="create-donor-form">
                <div className="grid grid-cols-2 gap-4">
                    <Controller control={props.form.control} name="firstName" render={({field, fieldState}) => (
                        <Field data-invalid={fieldState.invalid}>
                            <FieldLabel htmlFor="firstName">First Name</FieldLabel>
                            <Input {...field}
                                   aria-invalid={fieldState.invalid}
                                   id="firstName"
                                   placeholder="John"
                            />
                            {fieldState.invalid && (<FieldError errors={[fieldState.error]}/>)}
                        </Field>)}/>
                    <Controller control={props.form.control} name="lastName" render={({field, fieldState}) => (
                        <Field data-invalid={fieldState.invalid}>
                            <FieldLabel htmlFor="lastName">Last Name</FieldLabel>
                            <Input {...field}
                                   aria-invalid={fieldState.invalid}
                                   id="lastName"
                                   placeholder="Doe"
                            />
                            {fieldState.invalid && (<FieldError errors={[fieldState.error]}/>)}
                        </Field>)}/>
                </div>
                <Controller control={props.form.control} name="address" render={({field, fieldState}) => (
                    <Field data-invalid={fieldState.invalid}>
                        <FieldLabel htmlFor="address">Address</FieldLabel>
                        <Input {...field}
                               aria-invalid={fieldState.invalid}
                               id="address"
                               placeholder="123 Main St. Anytown, USA"
                        />
                        {fieldState.invalid && (<FieldError errors={[fieldState.error]}/>)}
                    </Field>)}/>
                <Controller control={props.form.control} name="phoneNumber" render={({field, fieldState}) => (
                    <Field data-invalid={fieldState.invalid}>
                        <FieldLabel htmlFor="phoneNumber">Phone Number</FieldLabel>
                        <Input {...field}
                               aria-invalid={fieldState.invalid}
                               id="phoneNumber"
                               placeholder="+123-456-789"
                        />
                        {fieldState.invalid && (<FieldError errors={[fieldState.error]}/>)}
                    </Field>)}/>
            </form>
        </FieldGroup>
    );
}

export function RegisterDonorButton() {

    const form = useForm<DonorFormSchema>({
        resolver: zodResolver(donorFormSchema),
        values: {
            firstName: "",
            lastName: "",
            address: "",
            phoneNumber: ""
        },
        mode: "onChange"
    });

    const [isDialogOpen, setDialogOpen] = useState(false);

    const fetcher = useFetcher({key: "register-donor"});
    const toastId = useRef<string | number | null>(null);

    const handleDialog = useCallback((state: boolean) => {
        form.reset();
        setDialogOpen(state);
    }, [form]);

    const formSubmit = useCallback((values: DonorFormSchema) => {
        handleDialog(false);
        toastId.current = toast.loading("Creating charity...");

        fetcher.submit({...values}, {
            action: "/api/donors", method: "post"
        });
    }, [fetcher, handleDialog]);

    useEffect(() => {
        if (fetcher.state === "idle" && fetcher.data && toastId.current) {

            if (!fetcher.data.ok) {
                toast.error(`Could not create donor`, {id: toastId.current});
            } else {
                toast.success("Created successfully!", {id: toastId.current});
            }
            toastId.current = null;
        }
    }, [fetcher.state, fetcher.data]);

    return (
        <Dialog onOpenChange={handleDialog} open={isDialogOpen}>
            <DialogTrigger asChild>
                <Button variant="outline" size="sm">
                    <PlusIcon
                    />
                    <span className="hidden lg:inline">Register Donor</span>
                </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-sm">
                <DialogHeader>
                    <DialogTitle>Register a new donor</DialogTitle>
                    <DialogDescription>
                        Create a new donor here. Click create when you&apos;re
                        done.
                    </DialogDescription>
                </DialogHeader>
                <CreateDonorForm form={form} formSubmit={formSubmit}/>
                <DialogFooter>
                    <DialogClose asChild>
                        <Button variant="outline" onClick={() => form.reset()}>Cancel</Button>
                    </DialogClose>
                    <Button type="submit" form="create-donor-form">Create</Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}