"use client"

import * as React from "react"
import {
    type ColumnDef,
    type ColumnFiltersState,
    flexRender,
    getCoreRowModel,
    getFilteredRowModel,
    getPaginationRowModel,
    useReactTable,
    type VisibilityState,
} from "@tanstack/react-table"
import {ChevronDownIcon, Columns3Icon, PlusIcon} from "lucide-react"

import {Button} from "~/components/ui/button"
import {
    DropdownMenu,
    DropdownMenuCheckboxItem,
    DropdownMenuContent,
    DropdownMenuTrigger,
} from "~/components/ui/dropdown-menu"
import {Input} from "~/components/ui/input"
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow,} from "~/components/ui/table"

import * as z from "zod"
import {Card, CardContent, CardHeader, CardTitle} from "~/components/ui/card";

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

const donors = [
    {
        id: 1,
        firstName: "John",
        lastName: "Doe",
        address: "123 Main St, Anytown, USA",
        phoneNumber: "555-1234",
        createdOn: "2023-01-15",
    },
    {
        id: 2,
        firstName: "Jane",
        lastName: "Smith",
        address: "456 Oak Ave, Anytown, USA",
        phoneNumber: "555-5678",
        createdOn: "2023-02-20",
    },
    {
        id: 3,
        firstName: "Peter",
        lastName: "Jones",
        address: "789 Pine Ln, Anytown, USA",
        phoneNumber: "555-9012",
        createdOn: "2023-03-10",
    },
];

export function DonorDataTable() {
    const [columnVisibility, setColumnVisibility] =
        React.useState<VisibilityState>({});
    const [columnFilters, setColumnFilters] = React.useState<ColumnFiltersState>(
        []
    );

    const table = useReactTable({
        data: donors,
        columns,
        onColumnFiltersChange: setColumnFilters,
        getCoreRowModel: getCoreRowModel(),
        getPaginationRowModel: getPaginationRowModel(),
        getFilteredRowModel: getFilteredRowModel(),
        onColumnVisibilityChange: setColumnVisibility,
        state: {
            columnFilters,
            columnVisibility,
        },
    })

    return (
        <div className="w-full grid grid-cols-1 lg:grid-cols-[65%_35%]">
            <div className="flex flex-col gap-4">
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
                        <Button variant="outline" size="sm">
                            <PlusIcon
                            />
                            <span className="hidden lg:inline">Register Donor</span>
                        </Button>
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
                                {table.getRowModel().rows?.length ? (
                                    table.getRowModel().rows.map((row) => (
                                        <TableRow
                                            key={row.id}
                                            data-state={row.getIsSelected() && "selected"}
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
                </div>
            </div>

            <div className="hidden lg:block">
                <Card>
                    <CardHeader>
                        <CardTitle>Donor Information</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-4">
                            <div>
                                <h3 className="font-semibold">Full Name</h3>
                                <p>John Doe</p>
                            </div>
                            <div>
                                <h3 className="font-semibold">Address</h3>
                                <p>123 Main St, Anytown, USA</p>
                            </div>
                            <div>
                                <h3 className="font-semibold">Phone Number</h3>
                                <p>555-1234</p>
                            </div>
                        </div>
                    </CardContent>
                </Card>
            </div>


        </div>
    )
}
