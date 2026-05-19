import * as React from "react"
import {useEffect, useRef, useState} from "react"
import {
    type ColumnDef,
    type ColumnFiltersState,
    flexRender,
    getCoreRowModel,
    useReactTable,
    type VisibilityState,
} from "@tanstack/react-table"
import {z} from "zod"
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
import {useFetcher} from "react-router";
import type {Charity, Page} from "~/routes/api/api.charities";
import {TargetUpdateCell} from "~/components/dashboard/table/target-update-cell";
import {ActionsCell} from "~/components/dashboard/table/actions-cell";
import {ProgressBadge} from "~/components/dashboard/table/progress-badge";
import {RegisterCharityButton} from "~/components/dashboard/table/register-charity-button";
import {CharityDrawerContent, TableCellViewer} from "~/components/dashboard/table/table-cell-viewer";
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
                <TableCellViewer item={row.original}
                                 drawerHandler={() => meta?.setSelectedItem && meta?.setSelectedItem(row.original)}/>
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

    const timeoutRef = useRef<NodeJS.Timeout | null>(null);

    const tableStateRef = useRef({pagination, columnFilters});

    useEffect(() => {
        tableStateRef.current = {pagination, columnFilters};
    });

    useEffect(() => {
        const sse = new EventSource("/api/event-stream");

        sse.addEventListener("charityevent", (event) => {
            console.log("Update received:", event.data);

            const {pagination: p, columnFilters: filters} = tableStateRef.current;

            const nameFilter = filters.find(f => f.id === 'name');
            const search = nameFilter ? String(nameFilter.value) : '';

            const params = new URLSearchParams({
                page: String(p.pageIndex),
                size: String(p.pageSize),
                search: search
            });

            if (timeoutRef.current) clearTimeout(timeoutRef.current);

            timeoutRef.current = setTimeout(() => {
                console.log("Firing fetcher.load!");
                fetcher.load(`/api/charities?${params.toString()}`);
            }, 500);
        });

        return () => {
            sse.close();
            if (timeoutRef.current) clearTimeout(timeoutRef.current);
        };
    }, [fetcher]);

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
                    <TableColumnToggleButton table={table}/>
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
                    <TableNavigationBar table={table}/>
                </div>
            </div>
        </div>
    )
}

