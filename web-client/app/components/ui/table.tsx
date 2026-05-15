import * as React from "react"

import {cn} from "~/lib/utils"
import {type Table as ReactTableType} from "@tanstack/react-table";
import {
    DropdownMenu,
    DropdownMenuCheckboxItem,
    DropdownMenuContent,
    DropdownMenuTrigger
} from "~/components/ui/dropdown-menu";
import {Button} from "~/components/ui/button";
import {
    ChevronDownIcon,
    ChevronLeftIcon,
    ChevronRightIcon,
    ChevronsLeftIcon,
    ChevronsRightIcon,
    Columns3Icon
} from "lucide-react";
import {Label} from "~/components/ui/label";
import {Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue} from "~/components/ui/select";

function Table({className, ...props}: React.ComponentProps<"table">) {
    return (
        <div
            data-slot="table-container"
            className="relative w-full overflow-x-auto"
        >
            <table
                data-slot="table"
                className={cn("w-full caption-bottom text-sm", className)}
                {...props}
            />
        </div>
    )
}

function TableHeader({className, ...props}: React.ComponentProps<"thead">) {
    return (
        <thead
            data-slot="table-header"
            className={cn("[&_tr]:border-b", className)}
            {...props}
        />
    )
}

function TableBody({className, ...props}: React.ComponentProps<"tbody">) {
    return (
        <tbody
            data-slot="table-body"
            className={cn("[&_tr:last-child]:border-0", className)}
            {...props}
        />
    )
}

function TableFooter({className, ...props}: React.ComponentProps<"tfoot">) {
    return (
        <tfoot
            data-slot="table-footer"
            className={cn(
                "border-t bg-muted/50 font-medium [&>tr]:last:border-b-0",
                className
            )}
            {...props}
        />
    )
}

function TableRow({className, ...props}: React.ComponentProps<"tr">) {
    return (
        <tr
            data-slot="table-row"
            className={cn(
                "border-b transition-colors hover:bg-muted/50 has-aria-expanded:bg-muted/50 data-[state=selected]:bg-muted",
                className
            )}
            {...props}
        />
    )
}

function TableHead({className, ...props}: React.ComponentProps<"th">) {
    return (
        <th
            data-slot="table-head"
            className={cn(
                "h-10 px-2 text-left align-middle font-medium whitespace-nowrap text-foreground [&:has([role=checkbox])]:pr-0",
                className
            )}
            {...props}
        />
    )
}

function TableCell({className, ...props}: React.ComponentProps<"td">) {
    return (
        <td
            data-slot="table-cell"
            className={cn(
                "p-2 align-middle whitespace-nowrap [&:has([role=checkbox])]:pr-0",
                className
            )}
            {...props}
        />
    )
}

function TableCaption({
                          className,
                          ...props
                      }: React.ComponentProps<"caption">) {
    return (
        <caption
            data-slot="table-caption"
            className={cn("mt-4 text-sm text-muted-foreground", className)}
            {...props}
        />
    )
}

export {
    Table,
    TableHeader,
    TableBody,
    TableFooter,
    TableHead,
    TableRow,
    TableCell,
    TableCaption,
}

export function TableColumnToggleButton<TData>(props: {
    table: ReactTableType<TData>,
}) {
    return <DropdownMenu>
        <DropdownMenuTrigger asChild>
            <Button variant="outline" size="sm">
                <Columns3Icon data-icon="inline-start"/>
                Columns
                <ChevronDownIcon data-icon="inline-end"/>
            </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end" className="w-32">
            {props.table
                .getAllColumns()
                .filter((column) =>
                    typeof column.accessorFn !== "undefined" &&
                    column.getCanHide())
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
                    );
                })
            }
        </DropdownMenuContent>
    </DropdownMenu>;
}

export function TableNavigationBar<TData>(props: {
    table: ReactTableType<TData>,
}) {
    return <div className="flex w-full items-center gap-8 lg:w-fit">
        <div className="hidden items-center gap-2 lg:flex">
            <Label htmlFor="rows-per-page" className="text-sm font-medium">
                Rows per page
            </Label>
            <Select
                value={`${props.table.getState().pagination.pageSize}`}
                onValueChange={(value) => {
                    props.table.setPageSize(Number(value))
                }}
            >
                <SelectTrigger size="sm" className="w-20" id="rows-per-page">
                    <SelectValue
                        placeholder={props.table.getState().pagination.pageSize}
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
            Page {props.table.getState().pagination.pageIndex + 1} of{" "}
            {props.table.getPageCount()}
        </div>
        <div className="ml-auto flex items-center gap-2 lg:ml-0">
            <Button
                variant="outline"
                className="hidden h-8 w-8 p-0 lg:flex"
                onClick={() => props.table.setPageIndex(0)}
                disabled={!props.table.getCanPreviousPage()}
            >
                <span className="sr-only">Go to first page</span>
                <ChevronsLeftIcon
                />
            </Button>
            <Button
                variant="outline"
                className="size-8"
                size="icon"
                onClick={() => props.table.previousPage()}
                disabled={!props.table.getCanPreviousPage()}
            >
                <span className="sr-only">Go to previous page</span>
                <ChevronLeftIcon
                />
            </Button>
            <Button
                variant="outline"
                className="size-8"
                size="icon"
                onClick={() => props.table.nextPage()}
                disabled={!props.table.getCanNextPage()}
            >
                <span className="sr-only">Go to next page</span>
                <ChevronRightIcon
                />
            </Button>
            <Button
                variant="outline"
                className="hidden size-8 lg:flex"
                size="icon"
                onClick={() => props.table.setPageIndex(props.table.getPageCount() - 1)}
                disabled={!props.table.getCanNextPage()}
            >
                <span className="sr-only">Go to last page</span>
                <ChevronsRightIcon
                />
            </Button>
        </div>
    </div>;
}