import {type ChartConfig, ChartContainer, ChartTooltip, ChartTooltipContent} from "~/components/ui/chart";
import {Area, AreaChart, CartesianGrid, XAxis, YAxis} from "recharts";
import {Separator} from "~/components/ui/separator";
import {TrendingDownIcon, TrendingUpIcon} from "lucide-react";
import * as React from "react";

const chartConfig = {
    totalAmount: {
        label: "Total amount",
        color:
            "var(--primary)",
    }
    ,
    donorCount: {
        label: "Donors",
        color:
            "var(--primary)",
    }
    ,
} satisfies ChartConfig

export function ChartArea(props: {
    data: { month: string; donorCount: number; totalAmount: number }[],
}) {

    const lastMonth = props.data[props.data.length - 1].totalAmount;
    const previousMonth = props.data.length > 1 ? props.data[props.data.length - 2].totalAmount : 0;
    const change = ((lastMonth - previousMonth) / previousMonth) * 100;

    const formattedChange = new Intl.NumberFormat('en-US', {
        signDisplay: 'exceptZero',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(change);


    return <>
        <ChartContainer config={chartConfig}>
            <AreaChart
                accessibilityLayer
                data={props.data}
                margin={{
                    left: 0,
                    right: 0,
                }}
            >
                <CartesianGrid vertical={false}/>
                <XAxis
                    dataKey="month"
                    tickLine={false}
                    axisLine={false}
                    tickMargin={8}
                    tickFormatter={(value) => value.slice(0, 3)}
                    hide
                />

                <YAxis yAxisId="left" orientation="left" hide/>
                <YAxis yAxisId="right" orientation="right" hide/>

                <ChartTooltip
                    cursor={false}
                    content={<ChartTooltipContent indicator="dot"/>}
                />

                <Area
                    yAxisId="left"
                    dataKey="donorCount"
                    type="natural"
                    fill="var(--color-donorCount)"
                    fillOpacity={0.6}
                    stroke="var(--color-donorCount)"
                />

                <Area
                    yAxisId="right"
                    dataKey="totalAmount"
                    type="natural"
                    fill="var(--color-totalAmount)"
                    fillOpacity={0.4}
                    stroke="var(--color-totalAmount)"
                />
            </AreaChart>
        </ChartContainer>
        <Separator/>
        <div className="grid gap-2">
            <div className="flex gap-2 leading-none font-medium">
                Raised amount {change > 0 ? "up" : "down"} by {formattedChange}% this month{" "}
                {change > 0 ? <TrendingUpIcon className="size-4"/> : <TrendingDownIcon className="size-4"/>}
            </div>
            <div className="text-muted-foreground">
                compared to last month's performance
            </div>
        </div>
        <Separator/>
    </>;
}