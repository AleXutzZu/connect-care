import * as React from "react"
import {
    type ColumnDef,
    type ColumnFiltersState,
    flexRender,
    getCoreRowModel,
    useReactTable,
    type VisibilityState,
} from "@tanstack/react-table"
import {z} from "zod"
import {Button} from "~/components/ui/button"
import {
    DropdownMenu,
    DropdownMenuCheckboxItem,
    DropdownMenuContent,
    DropdownMenuTrigger,
} from "~/components/ui/dropdown-menu"
import {Input} from "~/components/ui/input"
import {Label} from "~/components/ui/label"
import {Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue,} from "~/components/ui/select"
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow,} from "~/components/ui/table"
import {
    ChevronDownIcon,
    ChevronLeftIcon,
    ChevronRightIcon,
    ChevronsLeftIcon,
    ChevronsRightIcon,
    Columns3Icon
} from "lucide-react"
import {useFetcher} from "react-router";
import type {Charity, Page} from "~/routes/api/api.charities";
import {TargetUpdateCell} from "~/components/dashboard/table/target-update-cell";
import {ActionsCell} from "~/components/dashboard/table/actions-cell";
import {ProgressBadge} from "~/components/dashboard/table/progress-badge";
import {RegisterCharityButton} from "~/components/dashboard/table/register-charity-button";
import {CharityDrawerContent, TableCellViewer} from "~/components/dashboard/table/table-cell-viewer";
import {useEffect, useState} from "react";
import type {RowData} from "@tanstack/table-core";
import {Drawer} from "~/components/ui/drawer";
import {useIsMobile} from "~/hooks/use-mobile";

declare module '@tanstack/react-table' {
    interface TableMeta<TData extends RowData> {
        setSelectedItem: (item: TData) => void;
    }
}

export const charityColumnSchema = z.object({
    id: z.number(),
    name: z.string(),
    raisedAmount: z.number(),
    target: z.number(),
    registeredBy: z.string(),
});

const columns: ColumnDef<z.infer<typeof charityColumnSchema>>[] = [
    {
        accessorKey: "name",
        header: "Name",
        cell: ({row, table}) => {
            const meta = table.options.meta;

            return <div className="sm:min-w-75">
                <TableCellViewer item={row.original} drawerHandler={() => meta?.setSelectedItem && meta?.setSelectedItem(row.original)}/>
            </div>
        },
        enableHiding: false,
    },
    {
        accessorKey: "raisedAmount",
        header: () => (<div className="w-full text-right">Raised</div>),
        cell: ({row}) => (
            <div className="text-right">
                {new Intl.NumberFormat("en-US", {
                    style: "currency",
                    currency: "USD",
                }).format(row.original.raisedAmount)}
            </div>
        ),
    },
    {
        accessorKey: "target",
        header: () => (<div className="w-full text-right">Target</div>),
        cell: TargetUpdateCell
    },
    {
        accessorKey: "progress",
        header: () => (<div className="w-full text-right">Progress</div>),
        cell: ({row}) => {
            const progress = row.original.target === 0 ? 0 : (row.original.raisedAmount / row.original.target) * 100
            return (
                <div className="flex justify-end">
                    <ProgressBadge progress={progress}/>
                </div>
            );
        },
    },
    {
        accessorKey: "registeredBy",
        header: () => (<div className="w-full text-right">Registered by</div>),
        cell: ({row}) => {
            return <div className="text-right">{row.original.registeredBy}</div>;
        },
    },
    {
        id: "actions",
        cell: ActionsCell
    },

]

export function CharityDataTable() {
    const [data, setData] = useState<Charity[]>([]);
    const [columnVisibility, setColumnVisibility] =
        React.useState<VisibilityState>({registeredBy: false})
    const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>(
        []
    );
    const [pagination, setPagination] = useState({
        pageIndex: 0,
        pageSize: 10,
    });

    const [pageCount, setPageCount] = useState(0);

    const fetcher = useFetcher({key: "table"});

    useEffect(() => {
        const nameFilter = columnFilters.find(f => f.id === 'name');
        const nameValue: string = nameFilter ? (nameFilter.value as string) : '';

        const params = new URLSearchParams({
            page: String(pagination.pageIndex),
            size: String(pagination.pageSize),
            search: nameValue
        });

        const timeoutId = setTimeout(() => {
            fetcher.load(`/api/charities?${params.toString()}`);
        }, 300);

        return () => clearTimeout(timeoutId);

    }, [pagination, columnFilters]);


    useEffect(() => {
        if (fetcher.state === "idle" && fetcher.data) {
            const fetchedData = fetcher.data as Page<Charity>;

            setPageCount(fetchedData.totalPages);
            setData(fetchedData.content);
        }
    }, [fetcher.state, fetcher.data]);

    const [selectedItem, setSelectedItem] = useState<z.infer<typeof charityColumnSchema> | null>(null);
    const isMobile = useIsMobile();

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
        meta: {
            setSelectedItem: (item) => setSelectedItem(item)
        }
    });


    return (
        <div className="w-full flex-col justify-start gap-4 flex">

            <Drawer
                direction={isMobile ? "bottom" : "right"}
                open={!!selectedItem}
                onOpenChange={(open) => {
                    if (!open) setSelectedItem(null);
                }}
            >
                {selectedItem && (
                    <CharityDrawerContent
                        item={selectedItem}
                        setIsOpen={(open) => !open && setSelectedItem(null)}
                    />
                )}
            </Drawer>

            <div className="flex items-center justify-between px-4 lg:px-6 gap-2">
                <Input
                    placeholder="Filter by name..."
                    value={(table.getColumn("name")?.getFilterValue() as string) ?? ""}
                    onChange={(event) =>
                        table.getColumn("name")?.setFilterValue(event.target.value)
                    }
                    className="max-w-lg"
                />
                <div className="flex items-center gap-2">
                    <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                            <Button variant="outline" size="sm">
                                <Columns3Icon data-icon="inline-start"/>
                                Columns
                                <ChevronDownIcon data-icon="inline-end"/>
                            </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end" className="w-32">
                            {table
                                .getAllColumns()
                                .filter(
                                    (column) =>
                                        typeof column.accessorFn !== "undefined" &&
                                        column.getCanHide()
                                )
                                .map((column) => {
                                    const columnName = column.id.replace(/([A-Z])/g, " $1")
                                    return (
                                        <DropdownMenuCheckboxItem
                                            key={column.id}
                                            className="capitalize"
                                            checked={column.getIsVisible()}
                                            onCheckedChange={(value) =>
                                                column.toggleVisibility(!!value)
                                            }
                                        >
                                            {columnName}
                                        </DropdownMenuCheckboxItem>
                                    )
                                })}
                        </DropdownMenuContent>
                    </DropdownMenu>
                    <RegisterCharityButton/>
                </div>
            </div>
            <div
                className="relative flex flex-col gap-4 overflow-auto px-4 lg:px-6"
            >
                <div className="overflow-hidden rounded-lg border">
                    <Table>
                        <TableHeader className="sticky top-0 z-10 bg-muted">
                            {table.getHeaderGroups().map((headerGroup) => (
                                <TableRow key={headerGroup.id}>
                                    {headerGroup.headers.map((header) => {
                                        return (
                                            <TableHead key={header.id} colSpan={header.colSpan}>
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
                        <TableBody className="**:data-[slot=table-cell]:first:w-8">
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
                                        colSpan={table.getVisibleLeafColumns().length}
                                        className="h-24 text-center text-gray-500"
                                    >
                                        No results found.
                                    </TableCell>
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                </div>
                <div className="flex items-center justify-end px-4">
                    <div className="flex w-full items-center gap-8 lg:w-fit">
                        <div className="hidden items-center gap-2 lg:flex">
                            <Label htmlFor="rows-per-page" className="text-sm font-medium">
                                Rows per page
                            </Label>
                            <Select
                                value={`${table.getState().pagination.pageSize}`}
                                onValueChange={(value) => {
                                    table.setPageSize(Number(value))
                                }}
                            >
                                <SelectTrigger size="sm" className="w-20" id="rows-per-page">
                                    <SelectValue
                                        placeholder={table.getState().pagination.pageSize}
                                    />
                                </SelectTrigger>
                                <SelectContent side="top">
                                    <SelectGroup>
                                        {[10, 20, 30, 40, 50].map((pageSize) => (
                                            <SelectItem key={pageSize} value={`${pageSize}`}>
                                                {pageSize}
                                            </SelectItem>
                                        ))}
                                    </SelectGroup>
                                </SelectContent>
                            </Select>
                        </div>
                        <div className="flex w-fit items-center justify-center text-sm font-medium">
                            Page {table.getState().pagination.pageIndex + 1} of{" "}
                            {table.getPageCount()}
                        </div>
                        <div className="ml-auto flex items-center gap-2 lg:ml-0">
                            <Button
                                variant="outline"
                                className="hidden h-8 w-8 p-0 lg:flex"
                                onClick={() => table.setPageIndex(0)}
                                disabled={!table.getCanPreviousPage()}
                            >
                                <span className="sr-only">Go to first page</span>
                                <ChevronsLeftIcon
                                />
                            </Button>
                            <Button
                                variant="outline"
                                className="size-8"
                                size="icon"
                                onClick={() => table.previousPage()}
                                disabled={!table.getCanPreviousPage()}
                            >
                                <span className="sr-only">Go to previous page</span>
                                <ChevronLeftIcon
                                />
                            </Button>
                            <Button
                                variant="outline"
                                className="size-8"
                                size="icon"
                                onClick={() => table.nextPage()}
                                disabled={!table.getCanNextPage()}
                            >
                                <span className="sr-only">Go to next page</span>
                                <ChevronRightIcon
                                />
                            </Button>
                            <Button
                                variant="outline"
                                className="hidden size-8 lg:flex"
                                size="icon"
                                onClick={() => table.setPageIndex(table.getPageCount() - 1)}
                                disabled={!table.getCanNextPage()}
                            >
                                <span className="sr-only">Go to last page</span>
                                <ChevronsRightIcon
                                />
                            </Button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

