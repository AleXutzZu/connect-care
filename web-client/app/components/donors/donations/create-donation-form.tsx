import type {DonorWithoutDonations} from "~/routes/api/api.donors";
import type {ReactElement} from "react";
import {
    Dialog, DialogClose,
    DialogContent,
    DialogDescription, DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger
} from "~/components/ui/dialog";
import {Field, FieldDescription, FieldGroup} from "~/components/ui/field";
import {Label} from "~/components/ui/label";
import {Input} from "~/components/ui/input";
import {Button} from "~/components/ui/button";
import {Sheet, SheetContent, SheetDescription, SheetHeader, SheetTitle, SheetTrigger} from "~/components/ui/sheet";
import {CreateDonorForm} from "~/components/donors/register-donor-button";
import {useForm} from "react-hook-form";
import {donorFormSchema, type DonorFormSchema} from "~/lib/form-schemas";
import {zodResolver} from "@hookform/resolvers/zod";

export function CreateDonationForm(props: { donor?: DonorWithoutDonations, actionButton: ReactElement }) {
    return (
        <Dialog>
            <DialogTrigger asChild>
                {props.actionButton}
            </DialogTrigger>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Register donation</DialogTitle>
                    <DialogDescription>
                        Register a new charity donation for a donor
                    </DialogDescription>
                </DialogHeader>
                <Sheet>
                    <FieldGroup>
                        <Field>
                            <Label htmlFor="name-1">Donor</Label>
                            <Input id="name-1" name="name" defaultValue="Pedro Duarte"/>
                            {!props.donor && (
                                <FieldDescription>
                                    Not found? Register a new donor by clicking <SheetTrigger asChild>
                                    <Button variant="link" className="p-0">here</Button>
                                </SheetTrigger>
                                </FieldDescription>
                            )}
                        </Field>
                        <Field>
                            <Label htmlFor="username-1">Charity</Label>
                            <Input id="username-1" name="username" defaultValue="ABC"/>
                        </Field>

                        <Field>
                            <Label htmlFor="username-1">Amount</Label>
                            <Input id="username-1" name="username" defaultValue="1000"/>
                        </Field>
                    </FieldGroup>

                    <SheetContent>
                        <SheetHeader>
                            <SheetTitle>Register a new donor</SheetTitle>
                            <SheetDescription>Fill out the information below to register a new donor.</SheetDescription>
                        </SheetHeader>
                        <div className="px-4">
                            <CreateDonorSheetContent/>
                        </div>

                    </SheetContent>
                </Sheet>

                <DialogFooter>
                    <DialogClose asChild>
                        <Button variant="outline">Cancel</Button>
                    </DialogClose>
                    <Button type="submit">Register</Button>
                </DialogFooter>

            </DialogContent>
        </Dialog>
    );
}

function CreateDonorSheetContent() {
    const form = useForm<DonorFormSchema>({
        resolver: zodResolver(donorFormSchema)
    })

    return (
        <CreateDonorForm form={form} formSubmit={() => {
        }}/>
    )
}