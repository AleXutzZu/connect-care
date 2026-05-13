import {Controller, useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import * as React from "react";
import {useCallback, useEffect, useRef, useState} from "react";
import {useFetcher} from "react-router";
import {toast} from "sonner";
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
import {Button} from "~/components/ui/button";
import {PlusIcon} from "lucide-react";
import {Field, FieldError, FieldGroup, FieldLabel} from "~/components/ui/field";
import {Input} from "~/components/ui/input";
import * as z from "zod";

export function RegisterCharityButton() {
    const formSchema = z.object({
        name: z.string()
            .min(3, "Charity name must be at least 3 characters")
            .max(50, "Charity name must be at most 50 characters"),
        target: z.number("Target must be a number").positive("Target must be a positive number")
    });

    type FormValues = z.infer<typeof formSchema>;

    const form = useForm<FormValues>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            name: "",
            target: 1000
        },
        mode: "onChange"
    });

    const [isDialogOpen, setDialogOpen] = useState(false);

    const fetcher = useFetcher({key: "register-charity"});
    const toastId = useRef<string | number | null>(null);

    const handleDialog = useCallback((state: boolean) => {
        form.reset();
        setDialogOpen(state);
    }, [form]);

    const formSubmit = useCallback((values: FormValues) => {
        handleDialog(false);
        toastId.current = toast.loading("Creating charity...");

        fetcher.submit({name: values.name, target: values.target}, {
            action: "/api/charities", method: "post"
        });
    }, [fetcher, handleDialog]);


    useEffect(() => {
        if (fetcher.state === "idle" && fetcher.data && toastId.current) {

            if (!fetcher.data.ok) {
                toast.error(`Could not create charity`, {id: toastId.current});
            } else {
                toast.success("Created successfully!", {id: toastId.current});
            }
            toastId.current = null;
        }
    }, [fetcher.state, fetcher.data]);

    return (
        <Dialog onOpenChange={handleDialog} open={isDialogOpen}>
            <form onSubmit={form.handleSubmit(formSubmit)} id="create-charity-form">
                <DialogTrigger asChild>
                    <Button variant="outline" size="sm">
                        <PlusIcon
                        />
                        <span className="hidden lg:inline">Register Charity</span>
                    </Button>
                </DialogTrigger>
                <DialogContent className="sm:max-w-sm">
                    <DialogHeader>
                        <DialogTitle>Register a new charity</DialogTitle>
                        <DialogDescription>
                            Create a new charity here. Click create when you&apos;re
                            done.
                        </DialogDescription>
                    </DialogHeader>
                    <FieldGroup>
                        <Controller control={form.control} name="name" render={({field, fieldState}) => (
                            <Field data-invalid={fieldState.invalid}>
                                <FieldLabel htmlFor="name">Name</FieldLabel>
                                <Input {...field}
                                       aria-invalid={fieldState.invalid}
                                       id="name"
                                       placeholder="Sarcoma Foundation"
                                />
                                {fieldState.invalid && (<FieldError errors={[fieldState.error]}/>)}
                            </Field>)}/>

                        <Controller control={form.control} name="target" render={({field, fieldState}) => (
                            <Field data-invalid={fieldState.invalid}>
                                <FieldLabel htmlFor="target">Target</FieldLabel>
                                <Input {...field}
                                       onChange={(e) => {
                                           const value = e.target.value;
                                           if (value === "") field.onChange("");
                                           else field.onChange(isNaN(Number(value)) ? value : Number(value));
                                       }}
                                       aria-invalid={fieldState.invalid}
                                       id="target"
                                       placeholder="10000"
                                />
                                {fieldState.invalid && (<FieldError errors={[fieldState.error]}/>)}
                            </Field>)}/>
                    </FieldGroup>
                    <DialogFooter>
                        <DialogClose asChild>
                            <Button variant="outline" onClick={() => form.reset()}>Cancel</Button>
                        </DialogClose>
                        <Button type="submit" form="create-charity-form">Create</Button>
                    </DialogFooter>
                </DialogContent>
            </form>
        </Dialog>
    );
}