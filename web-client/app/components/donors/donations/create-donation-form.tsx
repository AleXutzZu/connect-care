import type {DonorWithoutDonations} from "~/routes/api/api.donors";
import React, {type ReactElement, useCallback, useEffect, useMemo, useRef, useState} from "react";
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
import {Input} from "~/components/ui/input";
import {Button} from "~/components/ui/button";
import {CreateDonorForm} from "~/components/donors/register-donor-button";
import {Controller, type ControllerRenderProps, useForm} from "react-hook-form";
import {donationFormSchema, type DonationFormSchema, donorFormSchema, type DonorFormSchema} from "~/lib/form-schemas";
import {zodResolver} from "@hookform/resolvers/zod";
import {Check, ChevronsUpDown, Loader2} from "lucide-react";
import {useFetcher} from "react-router";
import {Popover, PopoverContent, PopoverTrigger} from "~/components/ui/popover";
import {Command, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList} from "~/components/ui/command";
import {cn} from "~/lib/utils";
import type {Charity} from "~/routes/api/api.charities";
import {toast} from "sonner";
import {
    Drawer,
    DrawerClose,
    DrawerContent,
    DrawerDescription,
    DrawerFooter,
    DrawerHeader,
    DrawerTitle,
    DrawerTrigger
} from "~/components/ui/drawer";
import {useIsMobile} from "~/hooks/use-mobile";

type SearchableComboboxProps<T> = {
    value: any;
    onChange: (...event: any[]) => void;
    getUrl: (searchQuery: string) => string;
    renderResult: (item: T) => React.ReactElement;
    renderValue: (selectedItem: T | null) => string;
    itemKey: (item: T | null) => string | number | null;
    searchPlaceholder?: string;
    noResultsMessage?: string;
    initialMessage?: string;
    fetcherKey: string,
};

function SearchableCombobox<T>(props: SearchableComboboxProps<T>) {
    const {
        value,
        onChange,
        getUrl,
        renderResult,
        renderValue,
        itemKey,
        searchPlaceholder = "Type to search...",
        noResultsMessage = "No results found.",
        initialMessage = "Start typing to search",
        fetcherKey
    } = props;

    const [open, setOpen] = useState(false);
    const [searchQuery, setSearchQuery] = useState("");

    const fetcher = useFetcher<T[]>({key: fetcherKey});

    const allItems = fetcher.data ?? [];
    const isLoading = fetcher.state !== "idle";

    const selectedItem = useMemo(() => {
        return allItems.find(item => itemKey(item) === value) ?? null;
    }, [value, itemKey]);

    useEffect(() => {
        const currentName = selectedItem ? renderValue(selectedItem) : "";
        if (searchQuery.trim() === "" || searchQuery === currentName) return;

        const delayDebounce = setTimeout(() => {
            fetcher.load(getUrl(encodeURIComponent(searchQuery)));
        }, 300);

        return () => clearTimeout(delayDebounce);
    }, [searchQuery, itemKey(value)]);

    return (
        <Popover onOpenChange={setOpen} open={open} modal>
            <PopoverTrigger asChild>
                <Button
                    aria-expanded={open}
                    className="justify-between"
                    role="combobox"
                    variant="outline"
                >
                    {renderValue(selectedItem)}
                    <ChevronsUpDown className="ml-2 size-4 shrink-0 opacity-50"/>
                </Button>
            </PopoverTrigger>
            <PopoverContent className="p-0 w-(--radix-popover-trigger-width)">
                <Command shouldFilter={false}>
                    <CommandInput onValueChange={setSearchQuery} placeholder={searchPlaceholder} value={searchQuery}/>
                    <CommandList className="max-h-60 overflow-y-auto overflow-x-hidden">
                        {isLoading && (
                            <div className="flex items-center justify-center p-4">
                                <Loader2 className="size-4 animate-spin"/>
                                <span className="ml-2 text-muted-foreground text-sm">Searching...</span>
                            </div>
                        )}

                        {!searchQuery && !isLoading && allItems.length === 0 && (
                            <div className="p-4 text-center text-muted-foreground text-sm">
                                {initialMessage}
                            </div>
                        )}

                        {searchQuery && allItems.length === 0 && !isLoading && (
                            <CommandEmpty>{noResultsMessage}</CommandEmpty>
                        )}

                        {!isLoading && allItems.length > 0 && (
                            <CommandGroup>
                                {allItems.map((result) => (
                                    <CommandItem
                                        key={itemKey(result)}
                                        value={String(itemKey(result))}
                                        onSelect={() => {
                                            const val = itemKey(result);
                                            if (value === val) {
                                                onChange(null);
                                            } else {
                                                onChange(val);
                                            }
                                            setOpen(false);
                                        }}
                                    >
                                        <div className="flex items-start w-full">
                                            <Check
                                                className={cn(
                                                    "mr-2 size-4 mt-1 shrink-0",
                                                    value === itemKey(result) ? "opacity-100" : "opacity-0",
                                                )}
                                            />
                                            {renderResult(result)}
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
    const [openDialog, setOpenDialog] = useState(false);

    const form = useForm<DonationFormSchema>({
        resolver: zodResolver(donationFormSchema),
        mode: "onChange",
        defaultValues: {
            donorId: props.donor?.id
        },
    });

    const toastId = useRef<string | number | null>(null);
    const fetcher = useFetcher({key: "create-donation-form-fetcher"});

    const [preselectedDonor, setPreselectedDonor] = useState<DonorWithoutDonations | undefined>(props.donor);

    const handleDialog = useCallback((state: boolean) => {
        setPreselectedDonor(props.donor);
        setOpenDialog(state);
        form.reset();
    }, [form, props.donor]);

    const handleUpdatePreselectedDonor = useCallback((state: DonorWithoutDonations) => {
        setPreselectedDonor(state);
        form.setValue("donorId", state.id);
    }, [form]);

    const formSubmission = useCallback((values: DonationFormSchema) => {
        handleDialog(false);
        toastId.current = toast.loading("Creating donation...");

        fetcher.submit({...values}, {
            action: "/api/donations", method: "post"
        });
    }, [handleDialog, fetcher]);

    useEffect(() => {
        if (fetcher.state === "idle" && fetcher.data && toastId.current) {

            if (!fetcher.data.ok) {
                toast.error(`Could not create donation`, {id: toastId.current});
            } else {
                toast.success("Created successfully!", {id: toastId.current});
            }
            toastId.current = null;
        }
    }, [fetcher.state, fetcher.data]);


    return (
        <Dialog open={openDialog} onOpenChange={handleDialog}>
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
                <form id="create-donation-form" onSubmit={form.handleSubmit(formSubmission)}>
                    <FieldGroup>
                        <Controller render={({field, fieldState}) => (
                            <Field data-invalid={fieldState.invalid}>
                                <FieldLabel htmlFor="donorId">Donor</FieldLabel>
                                {preselectedDonor &&
                                    <Input disabled readOnly
                                           value={`${preselectedDonor.firstName} ${preselectedDonor.lastName}`}
                                           id="donorId"
                                           aria-invalid={fieldState.invalid}/>}
                                {!preselectedDonor && (
                                    <DonorSelectField field={field} setPreselectedDonor={handleUpdatePreselectedDonor}/>
                                )}
                                {fieldState.invalid && (<FieldError errors={[fieldState.error]}/>)}
                            </Field>
                        )} name="donorId" control={form.control}/>
                        <Controller render={({field, fieldState}) => (
                            <Field data-invalid={fieldState.invalid}>
                                <FieldLabel htmlFor="charityId">Charity</FieldLabel>

                                <SearchableCombobox<Charity>
                                    value={field.value}
                                    onChange={field.onChange}
                                    getUrl={query => `/api/charities?search=${query}`}
                                    itemKey={charity => charity?.id ?? null}
                                    renderValue={charity => charity ? `${charity.name}` : "Search for charities..."}
                                    renderResult={charity => (<p>{charity.name}</p>)}
                                    fetcherKey="search-charities-fetcher"
                                />

                                {fieldState.invalid && (<FieldError errors={[fieldState.error]}/>)}
                            </Field>
                        )} name="charityId" control={form.control}/>

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

function DonorSelectField(props: {
    field: ControllerRenderProps<DonationFormSchema, "donorId">,
    setPreselectedDonor: (donor: DonorWithoutDonations) => void
}) {
    const form = useForm<DonorFormSchema>({
        resolver: zodResolver(donorFormSchema),
    });

    const fetcher = useFetcher({key: "register-donor"});
    const toastId = useRef<string | number | null>(null);

    const formSubmit = useCallback((values: DonorFormSchema) => {
        toastId.current = toast.loading("Creating donor...");

        fetcher.submit({...values}, {
            action: "/api/donors", method: "post"
        });
    }, [fetcher]);

    useEffect(() => {
        if (fetcher.state === "idle" && fetcher.data && toastId.current) {

            if (!fetcher.data.ok) {
                toast.error(`Could not create donor`, {id: toastId.current});
            } else {
                toast.success("Created successfully!", {id: toastId.current});
                props.setPreselectedDonor(fetcher.data.data as DonorWithoutDonations);
            }
            toastId.current = null;
        }
    }, [fetcher.state, fetcher.data]);

    const [drawerOpen, setDrawerOpen] = useState(false);

    const handleDrawer = useCallback((state: boolean) => {
        form.reset();
        setDrawerOpen(state);
    }, [form]);

    const isMobile = useIsMobile();

    return (
        <Drawer open={drawerOpen} onOpenChange={handleDrawer} direction={isMobile ? "bottom" : "right"}>
            <SearchableCombobox<DonorWithoutDonations>
                value={props.field.value}
                onChange={props.field.onChange}
                getUrl={query => `/api/donors?search=${query}`}
                itemKey={donor => donor?.id ?? null}
                renderValue={donor => donor ? `${donor.firstName} ${donor.lastName}` : "Search for donors..."}
                renderResult={donor => (
                    <div className="flex flex-col">
                        <p className="font-medium text-base">
                            {donor.firstName} {donor.lastName}
                        </p>
                        <p className="text-sm text-muted-foreground">{donor.address}</p>
                        <p className="text-sm text-muted-foreground">{donor.phoneNumber}</p>
                    </div>
                )}
                fetcherKey="search-donors-fetcher"
            />
            <FieldDescription>
                Not found? Register a new donor by clicking <DrawerTrigger asChild>
                <Button variant="link" className="p-0">here</Button>
            </DrawerTrigger>
            </FieldDescription>

            <DrawerContent>
                <DrawerHeader>
                    <DrawerTitle>Register a new donor</DrawerTitle>
                    <DrawerDescription>Fill out the information below to register a new donor.</DrawerDescription>
                </DrawerHeader>
                <div className="px-4">
                    <CreateDonorForm form={form} formSubmit={formSubmit}/>
                </div>

                <DrawerFooter>
                    <Button type="submit" form="create-donor-form">Submit</Button>
                    <DrawerClose asChild>
                        <Button variant="outline">Done</Button>
                    </DrawerClose>
                </DrawerFooter>
            </DrawerContent>
        </Drawer>
    );
}