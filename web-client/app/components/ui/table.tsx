import * as React from "react"

import {cn} from "~/lib/utils"
import {useReactTable} from "@tanstack/react-table";
import {
    DropdownMenu,
    DropdownMenuCheckboxItem,
    DropdownMenuContent,
    DropdownMenuTrigger
} from "~/components/ui/dropdown-menu";
import {Button} from "~/components/ui/button";
import {ChevronDownIcon, Columns3Icon} from "lucide-react";

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
    table: ReturnType<typeof useReactTable<TData>>,
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