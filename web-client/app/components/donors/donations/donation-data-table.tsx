import {useEffect, useRef, useState} from "react";
import {
    type ColumnDef,
    type ColumnFiltersState,
    flexRender,
    getCoreRowModel,
    getFilteredRowModel,
    getPaginationRowModel,
    getSortedRowModel,
    type SortingState,
    useReactTable
} from "@tanstack/react-table";
import {useFetcher} from "react-router";
import type {Donation, Donor} from "~/routes/api/api.donor";
import {Input} from "~/components/ui/input";
import {Table, TableBody, TableCell, TableHead, TableHeader, TableNavigationBar, TableRow} from "~/components/ui/table";
import {Button} from "~/components/ui/button";
import {ArrowUpDown} from "lucide-react";
import {format} from "date-fns";
import {ActionsCell} from "~/components/donors/donations/actions-cell";

const columns: ColumnDef<Donation>[] = [
    {
        accessorKey: "charityName",
        header: ({column}) => {
            return (
                <Button
                    variant="ghost"
                    onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
                >
                    Charity
                    <ArrowUpDown className="ml-2 h-4 w-4"/>
                </Button>
            )
        },
        cell: ({row}) => (<div className="text-wrap">{row.original.charityName}</div>)
    },
    {
        accessorKey: "amount",
        header: ({column}) => {
            return (
                <Button
                    variant="ghost"
                    onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
                >
                    Amount
                    <ArrowUpDown className="ml-2 h-4 w-4"/>
                </Button>
            )
        },
        cell: ({row}) => {
            const formatted = new Intl.NumberFormat("en-US", {
                style: "currency",
                currency: "USD",
            }).format(row.original.amount)

            return <div className="text-right font-medium">{formatted}</div>
        }
    },
    {
        accessorKey: "createdOn",
        header: ({column}) => {
            return (
                <Button
                    variant="ghost"
                    onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
                >
                    Date
                    <ArrowUpDown className="ml-2 h-4 w-4"/>
                </Button>
            )
        },
        cell: ({row}) => (
            <div>{format(row.original.createdOn, "MMM dd, yyyy")}</div>
        )
    },
    {
        id: "actions",
        cell: ActionsCell
    }
];

export function DonationDataTable(props: { donorId: number }) {
    const [data, setData] = useState<Donation[]>([]);
    const [sorting, setSorting] = useState<SortingState>([
        {
            id: "createdOn",
            desc: true
        }
    ]);
    const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>(
        []
    );

    const table = useReactTable({
        data,
        columns,
        onSortingChange: setSorting,
        onColumnFiltersChange: setColumnFilters,
        getCoreRowModel: getCoreRowModel(),
        getPaginationRowModel: getPaginationRowModel(),
        getSortedRowModel: getSortedRowModel(),
        getFilteredRowModel: getFilteredRowModel(),
        state: {
            sorting,
            columnFilters,
        },
    });

    const fetcher = useFetcher({key: `donor-${props.donorId}-donation-table`});

    const isLoading = fetcher.state != "idle";

    useEffect(() => {
        if (fetcher.state === "idle" && fetcher.data) {
            const fetchedData = fetcher.data as Donor;

            setData(fetchedData.donations);
        }
    }, [fetcher.state, fetcher.data]);

    useEffect(() => {
        fetcher.load(`/api/donors/${props.donorId}`);
    }, [props.donorId]);


    const timeoutRef = useRef<NodeJS.Timeout | null>(null);


    useEffect(() => {
        const sse = new EventSource("/api/event-stream");

        sse.addEventListener("donationevent", (event) => {
            console.log("Update received:", event.data);

            if (timeoutRef.current) clearTimeout(timeoutRef.current);

            timeoutRef.current = setTimeout(() => {
                fetcher.load(`/api/donors/${props.donorId}`);
            }, 500);
        });

        return () => {
            sse.close();
            if (timeoutRef.current) clearTimeout(timeoutRef.current);
        };
    }, [fetcher]);

    return (
        <div className="w-full">
            <div className="flex items-center py-4 w-full">
                <Input
                    placeholder="Filter by charity..."
                    value={(table.getColumn("charityName")?.getFilterValue() as string) ?? ""}
                    onChange={(event) =>
                        table.getColumn("charityName")?.setFilterValue(event.target.value)
                    }
                    className="w-full"
                />
            </div>
            <div className="rounded-md border">
                <Table>
                    <TableHeader>
                        {table.getHeaderGroups().map((headerGroup) => (
                            <TableRow key={headerGroup.id}>
                                {headerGroup.headers.map((header) => {
                                    return (
                                        <TableHead key={header.id}>
                                            {header.isPlaceholder
                                                ? null
                                                : flexRender(
                                                    header.column.columnDef.header,
                                                    header.getContext()
                                                )}
                                        </TableHead>
                                    )
                                })}
                            </TableRow>
                        ))}
                    </TableHeader>
                    <TableBody>
                        {isLoading && data.length === 0 ? (
                            Array.from({length: 5}).map((_, rowIndex) => (
                                <TableRow key={`skeleton-row-${rowIndex}`}>
                                    <TableCell key={`skeleton-cell-${rowIndex}`}
                                               colSpan={table.getVisibleLeafColumns().length}>
                                        <div
                                            className="h-6 w-full animate-pulse rounded-md bg-gray-200 dark:bg-gray-800"/>
                                    </TableCell>
                                </TableRow>
                            ))

                        ) : table.getRowModel().rows?.length ? (
                            table.getRowModel().rows.map((row) => (
                                <TableRow key={row.id}>
                                    {row.getVisibleCells().map((cell) => (
                                        <TableCell key={cell.id}>
                                            {flexRender(
                                                cell.column.columnDef.cell,
                                                cell.getContext()
                                            )}
                                        </TableCell>
                                    ))}
                                </TableRow>
                            ))
                        ) : (
                            <TableRow>
                                <TableCell
                                    colSpan={columns.length}
                                    className="h-24 text-center"
                                >
                                    No results.
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </div>
            <div className="flex items-center justify-end space-x-2 py-4">
                <TableNavigationBar table={table}/>
            </div>
        </div>
    )
}