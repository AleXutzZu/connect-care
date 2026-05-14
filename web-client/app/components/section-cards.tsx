"use client"

import {Badge} from "~/components/ui/badge"
import {Card, CardAction, CardDescription, CardFooter, CardHeader, CardTitle,} from "~/components/ui/card"
import {TrendingDownIcon, TrendingUpIcon} from "lucide-react"
import * as React from "react";
import {useMemo} from "react";
import type {Statistics} from "~/routes/dashboard/home";
import {getDate, getMonth, getYear} from "date-fns";

interface HasDateField {
    date: Date | string
}

type StatsProps<T extends HasDateField> =
    { mode: "raw", data: T[], metric: keyof T, weight?: keyof T, title: string, formatOptions: Intl.NumberFormatOptions }
    |
    { mode: "precomputed", values: PrecomputedData, title: string, formatOptions: Intl.NumberFormatOptions };

interface ComparisonComputation {
    total: number,
    change: number,
    changeFormatted: string,
}


interface HasDateField {
    date: Date | string;
}

interface PrecomputedData {
    current: number;
    previous: number;
}

type ComputeOptions<T extends HasDateField> =
    | { mode: 'raw'; data: T[]; weight?: keyof T; metric: keyof T }
    | { mode: 'precomputed'; values: PrecomputedData };

function computeComparison<T extends HasDateField>(options: ComputeOptions<T>): ComparisonComputation {
    let current = 0;
    let previous = 0;

    if (options.mode === "raw") {
        let currentWeights = 0;
        let previousWeights = 0;
        const now = new Date();
        const currentDay = getDate(now);
        const currentMonth = getMonth(now);
        const currentYear = getYear(now);

        options.data.forEach(entry => {
            const entryDate = new Date(entry.date);
            const val = entry[options.metric] as unknown as number;
            const weight = options.weight ? (entry[options.weight] as unknown as number) : 0;

            if (getMonth(entryDate) === currentMonth && getYear(entryDate) === currentYear && getDate(entryDate) <= currentDay) {
                current += val;
                currentWeights += weight;
            }
            const prevMonth = currentMonth === 0 ? 11 : currentMonth - 1;
            const prevYear = currentMonth === 0 ? currentYear - 1 : currentYear;
            if (getMonth(entryDate) === prevMonth && getYear(entryDate) === prevYear && getDate(entryDate) <= currentDay) {
                previous += val;
                previousWeights += weight;
            }
        });

        if (currentWeights > 0 && previousWeights > 0) {
            current = current / currentWeights;
            previous = previous / previousWeights;
        }

    } else {
        current = options.values.current;
        previous = options.values.previous;
    }
    const formatter = new Intl.NumberFormat('en-US', {
        signDisplay: 'exceptZero',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
        style: "percent"
    });

    if (previous === 0) return {change: 0, total: current, changeFormatted: "0.00%"};
    const change = ((current - previous) / previous);

    return {
        change: change,
        total: current,
        changeFormatted: formatter.format(change)
    }
}

function StatsCard<T extends HasDateField>(props: StatsProps<T>) {
    const {change, total, changeFormatted} = useMemo(() => computeComparison(props), [props]);

    const valueFormatter = new Intl.NumberFormat('en-US', props.formatOptions);

    return (
        <Card className="@container/card">
            <CardHeader>
                <CardDescription>{props.title}</CardDescription>
                <CardTitle className="text-2xl font-semibold tabular-nums @[250px]/card:text-3xl">
                    {valueFormatter.format(total)}
                </CardTitle>
                <CardAction>
                    <Badge variant="outline">
                        {change > 0 && <TrendingUpIcon/>}
                        {change < 0 && <TrendingDownIcon/>}
                        {changeFormatted}
                    </Badge>
                </CardAction>
            </CardHeader>
            <CardFooter className="flex-col items-start gap-1.5 text-sm">
                <div className="line-clamp-1 flex gap-2 font-medium">
                    Trending {change > 0 ? "up" : "down"} this month{" "}
                    {change > 0 && <TrendingUpIcon className="size-4"/>}
                    {change < 0 && <TrendingDownIcon className="size-4"/>}
                </div>
                <div className="text-muted-foreground">
                    compared to last month
                </div>
            </CardFooter>
        </Card>
    );
}

export function SectionCards(props: Statistics) {
    const currencyFormatterOptions: Intl.NumberFormatOptions = {
        style: "currency",
        currency: "USD",
        maximumFractionDigits: 2,
        minimumFractionDigits: 2
    };

    const decimalFormatterOptions: Intl.NumberFormatOptions = {
        style: "decimal"
    };

    return (
        <div
            className="grid grid-cols-1 gap-4 px-4 *:data-[slot=card]:bg-gradient-to-t *:data-[slot=card]:from-primary/5 *:data-[slot=card]:to-card *:data-[slot=card]:shadow-xs lg:px-6 @xl/main:grid-cols-2 @5xl/main:grid-cols-4 dark:*:data-[slot=card]:bg-card">
            <StatsCard mode="raw" data={props.dailyDonations} metric="totalAmount" title="Total Raised"
                       key="total-raised" formatOptions={currencyFormatterOptions}/>

            <StatsCard mode="raw" title="Average Donation" formatOptions={currencyFormatterOptions}
                       data={props.dailyDonations} metric="totalAmount" weight="donationCount" key="avg-donation"/>

            <StatsCard mode="precomputed" title="New Donors" formatOptions={decimalFormatterOptions}
                       values={props.monthToDateDonors} key="new-conors"/>

            <StatsCard mode="precomputed" title="Active Donors" formatOptions={decimalFormatterOptions}
                       values={props.monthlyActiveDonors} key="active-donors"/>
        </div>
    )
}
