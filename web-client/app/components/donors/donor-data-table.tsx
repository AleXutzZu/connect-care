import * as React from "react"
import {useEffect, useMemo, useState} from "react"
import {
    type ColumnDef,
    type ColumnFiltersState,
    flexRender,
    getCoreRowModel,
    useReactTable,
    type VisibilityState,
} from "@tanstack/react-table"
import {Input} from "~/components/ui/input"
import {
    Table,
    TableBody,
    TableCell,
    TableColumnToggleButton,
    TableHead,
    TableHeader,
    TableNavigationBar,
    TableRow,
} from "~/components/ui/table"

import * as z from "zod"
import {useFetcher} from "react-router";
import type {Donor, Page} from "~/routes/api/api.donors";
import {DonorCard, EmptyDonorCard} from "~/components/donors/donor-card";
import {RegisterDonorButton} from "~/components/donors/register-donor-button";
import {DonorDrawer} from "~/components/donors/donor-drawer";

export const donorSchema = z.object({
    id: z.number(),
    firstName: z.string(),
    lastName: z.string(),
    address: z.string(),
    phoneNumber: z.string(),
    createdOn: z.string(),
})

const columns: ColumnDef<z.infer<typeof donorSchema>>[] = [
    {
        accessorKey: "fullName",
        header: "Full Name",
        cell: ({row}) => `${row.original.firstName} ${row.original.lastName}`,
        enableHiding: false
    },
    {
        accessorKey: "address",
        header: "Address",
    },
    {
        accessorKey: "phoneNumber",
        header: "Phone Number",
    },
];

export function DonorDataTable() {
    const [data, setData] = useState<Donor[]>([]);
    const [columnVisibility, setColumnVisibility] =
        React.useState<VisibilityState>({});
    const [columnFilters, setColumnFilters] = React.useState<ColumnFiltersState>(
        []
    );
    const [pagination, setPagination] = useState({
        pageIndex: 0,
        pageSize: 20,
    });

    const [pageCount, setPageCount] = useState(0);
    const fetcher = useFetcher({key: "donors-table"});

    useEffect(() => {
        const nameFilter = columnFilters.find(f => f.id === 'fullName');
        const nameValue: string = nameFilter ? (nameFilter.value as string) : '';

        const params = new URLSearchParams({
            page: String(pagination.pageIndex),
            size: String(pagination.pageSize),
            search: nameValue
        });

        const timeoutId = setTimeout(() => {
            fetcher.load(`/api/donors?${params.toString()}`);
        }, 300);

        return () => clearTimeout(timeoutId);

    }, [pagination, columnFilters]);

    useEffect(() => {
        if (fetcher.state === "idle" && fetcher.data) {
            const fetchedData = fetcher.data as Page<Donor>;

            setPageCount(fetchedData.totalPages);
            setData(fetchedData.content);
        }
    }, [fetcher.state, fetcher.data]);

    const table = useReactTable({
        data,
        columns,
        state: {
            columnVisibility,
            columnFilters,
            pagination,
        },
        pageCount: pageCount,
        getRowId: (row) => row.id.toString(),
        manualPagination: true,
        manualFiltering: true,
        onColumnFiltersChange: setColumnFilters,
        onColumnVisibilityChange: setColumnVisibility,
        onPaginationChange: setPagination,
        getCoreRowModel: getCoreRowModel(),
    });

    const [blockBackground, setBlockBackground] = useState(false);
    const [selectedDonorId, setSelectedDonorId] = useState<number | null>(null);

    const selectedDonor = useMemo(() => {
        if (selectedDonorId === null) return null;
        return data.find(s => s.id === selectedDonorId) ?? null;
    }, [selectedDonorId, data]);

    const isMobile = /*useIsMobile()*/ true;
    const [openDrawer, setOpenDrawer] = useState(false);

    return (
        <div className="w-full grid grid-cols-1 lg:grid-cols-[65%_35%]">
            <div className="flex flex-col gap-4">
                <div className="flex items-center justify-between px-4 lg:px-6 gap-2">
                    <Input
                        placeholder="Filter by name..."
                        value={(table.getColumn("fullName")?.getFilterValue() as string) ?? ""}
                        onChange={(event) =>
                            table.getColumn("fullName")?.setFilterValue(event.target.value)
                        }
                        className="max-w-lg"
                    />
                    <div className="flex items-center gap-2">
                        <TableColumnToggleButton table={table}/>
                        <RegisterDonorButton/>
                    </div>
                </div>
                <div className="relative flex flex-col gap-4 overflow-auto px-4 lg:px-6">
                    <div className="rounded-md border overflow-hidden">
                        <Table>
                            <TableHeader className="sticky top-0 z-10 bg-muted">
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
                                {fetcher.state !== "idle" && data.length === 0 ? (
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
                                        <TableRow
                                            key={row.id}
                                            role="button"
                                            onClick={() => {
                                                if (!blockBackground) {
                                                    setSelectedDonorId(row.original.id);
                                                    setOpenDrawer(true);
                                                }
                                            }}
                                            className="cursor-default"
                                        >
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
                    <div className="flex items-center justify-end px-4">
                        <TableNavigationBar table={table} pageSizes={[20, 30, 40, 50, 100]}/>
                    </div>
                </div>
            </div>

            <div className="hidden lg:block lg:pr-6">
                {selectedDonor && <DonorCard donor={selectedDonor} setBlockBackground={setBlockBackground}/>}
                {!selectedDonor && <EmptyDonorCard/>}
                {isMobile && selectedDonor &&
                    <DonorDrawer donor={selectedDonor} open={openDrawer} onOpenChange={setOpenDrawer}/>}
            </div>
        </div>
    )
}
