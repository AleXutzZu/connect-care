import type {DonorWithoutDonations} from "~/routes/api/api.donors";
import React, {type ReactElement, useCallback, useEffect, useMemo, useState} from "react";
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
import {Field, FieldDescription, FieldError, FieldGroup, FieldLabel} from "~/components/ui/field";
import {Label} from "~/components/ui/label";
import {Input} from "~/components/ui/input";
import {Button} from "~/components/ui/button";
import {Sheet, SheetContent, SheetDescription, SheetHeader, SheetTitle, SheetTrigger} from "~/components/ui/sheet";
import {CreateDonorForm} from "~/components/donors/register-donor-button";
import {Controller, useForm} from "react-hook-form";
import {donationFormSchema, type DonationFormSchema, donorFormSchema, type DonorFormSchema} from "~/lib/form-schemas";
import {zodResolver} from "@hookform/resolvers/zod";
import {Check, ChevronsUpDown, Loader2} from "lucide-react";
import {useFetcher} from "react-router";
import {Popover, PopoverContent, PopoverTrigger} from "~/components/ui/popover";
import {Command, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList} from "~/components/ui/command";
import {cn} from "~/lib/utils";

function SearchableCombobox(props: {
    selectedDonorId: number | null,
    setSelectedDonorId: ((value: (number | null) | ((val: number | null) => number | null)) => void)
}) {
    const [open, setOpen] = useState(false)
    const [searchQuery, setSearchQuery] = useState("");

    const fetcher = useFetcher<DonorWithoutDonations[]>();

    useEffect(() => {
        const currentName = selectedDonor ? `${selectedDonor.firstName} ${selectedDonor.lastName}` : "";
        if (searchQuery.trim() === "" || searchQuery === currentName) return;

        const delayDebounce = setTimeout(() => {
            fetcher.load(`/api/donors?search=${encodeURIComponent(searchQuery)}`);
        }, 300);

        return () => clearTimeout(delayDebounce);
    }, [searchQuery, props.selectedDonorId]);

    const results = fetcher.data ?? [];
    const isLoading = fetcher.state !== "idle";

    const selectedDonor = useMemo(() => {
        return results.find(donor => donor.id === props.selectedDonorId) ?? null;
    }, [props.selectedDonorId]);

    return (
        <Popover onOpenChange={setOpen} open={open} modal>
            <PopoverTrigger asChild>
                <Button
                    aria-expanded={open}
                    className="justify-between"
                    role="combobox"
                    variant="outline"
                >
                    {selectedDonor ? `${selectedDonor.firstName} ${selectedDonor.lastName}` : "Search for donors..."}
                    <ChevronsUpDown className="ml-2 size-4 shrink-0 opacity-50"/>
                </Button>
            </PopoverTrigger>
            <PopoverContent className="p-0 w-(--radix-popover-trigger-width)">
                <Command shouldFilter={false}>
                    <CommandInput onValueChange={setSearchQuery} placeholder="Type to search..." value={searchQuery}/>
                    <CommandList className="max-h-60 overflow-y-auto overflow-x-hidden">
                        {isLoading && (
                            <div className="flex items-center justify-center p-4">
                                <Loader2 className="size-4 animate-spin"/>
                                <span className="ml-2 text-muted-foreground text-sm">Searching...</span>
                            </div>
                        )}

                        {!searchQuery && !isLoading && (
                            <div className="p-4 text-center text-muted-foreground text-sm">
                                Start typing to search
                            </div>
                        )}

                        {searchQuery && results.length === 0 && !isLoading && (
                            <CommandEmpty>No results found.</CommandEmpty>
                        )}

                        {!isLoading && results.length > 0 && (
                            <CommandGroup>
                                {results.map((result) => (
                                    <CommandItem
                                        key={result.id}
                                        value={result.id.toString()}

                                        onSelect={() => {
                                            props.setSelectedDonorId(prev => prev === result.id ? null : result.id);
                                            setOpen(false);
                                        }}
                                    >
                                        <div className="flex items-start w-full">
                                            <Check
                                                className={cn(
                                                    "mr-2 size-4 mt-1 shrink-0",
                                                    props.selectedDonorId === result.id ? "opacity-100" : "opacity-0",
                                                )}
                                            />
                                            <div className="flex flex-col">
                                                <p className="font-medium text-base">
                                                    {result.firstName} {result.lastName}
                                                </p>
                                                <p className="text-sm text-muted-foreground">{result.address}</p>
                                                <p className="text-sm text-muted-foreground">{result.phoneNumber}</p>
                                            </div>
                                        </div>
                                    </CommandItem>
                                ))}
                            </CommandGroup>
                        )}
                    </CommandList>
                </Command>
            </PopoverContent>
        </Popover>
    );
}


export function CreateDonationForm(props: { donor?: DonorWithoutDonations, actionButton: ReactElement }) {

    const form = useForm<DonationFormSchema>({
        resolver: zodResolver(donationFormSchema),
        mode: "onChange",
        defaultValues: {
            donorId: props.donor?.id
        },
    });

    const formSubmission = useCallback((values: DonationFormSchema) => {
        console.log("Submitted values", values);
    }, []);

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
                    <form id="create-donation-form" onSubmit={form.handleSubmit(formSubmission)}>
                        <FieldGroup>
                            <Controller render={({field, fieldState}) => (

                                <Field data-invalid={fieldState.invalid}>
                                    <FieldLabel htmlFor="donorId">Donor</FieldLabel>
                                    {props.donor &&
                                        <Input disabled readOnly
                                               value={`${props.donor.firstName} ${props.donor.lastName}`} id="donorId"
                                               aria-invalid={fieldState.invalid}/>}
                                    {!props.donor && (
                                        <>
                                            <SearchableCombobox selectedDonorId={field.value}
                                                                 setSelectedDonorId={value => {
                                                                     if (value === null) form.resetField("donorId");
                                                                     else if (typeof value === "number") form.setValue("donorId", value)
                                                                     else {
                                                                         const newVal = value(form.getValues("donorId"));
                                                                         if (newVal === null) form.resetField("donorId");
                                                                         else form.setValue("donorId", newVal);
                                                                     }
                                                                 }}/>
                                            <FieldDescription>
                                                Not found? Register a new donor by clicking <SheetTrigger asChild>
                                                <Button variant="link" className="p-0">here</Button>
                                            </SheetTrigger>
                                            </FieldDescription>
                                        </>
                                    )}
                                    {fieldState.invalid && (<FieldError errors={[fieldState.error]}/>)}
                                </Field>

                            )} name="donorId" control={form.control}/>
                            <Field>
                                <Label htmlFor="username-1">Charity</Label>
                                <Input id="username-1" name="username" defaultValue="ABC"/>
                            </Field>

                            <Controller control={form.control} name="amount" render={({field, fieldState}) => (
                                <Field data-invalid={fieldState.invalid}>
                                    <FieldLabel htmlFor="amount">Amount</FieldLabel>
                                    <Input {...field}
                                           onChange={(e) => {
                                               const value = e.target.value;
                                               if (value === "") field.onChange("");
                                               else field.onChange(isNaN(Number(value)) ? value : Number(value));
                                           }}
                                           aria-invalid={fieldState.invalid}
                                           id="amount"
                                    />
                                    {fieldState.invalid && (<FieldError errors={[fieldState.error]}/>)}
                                </Field>)}/>
                        </FieldGroup>
                    </form>

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
                    <Button type="submit" form="create-donation-form">Register</Button>
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