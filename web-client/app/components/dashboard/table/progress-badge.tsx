import {Badge} from "~/components/ui/badge";
import {CheckCheck, CheckIcon, LoaderIcon, TriangleAlertIcon} from "lucide-react";
import * as React from "react";

export function ProgressBadge({progress}: { progress: number }) {
    if (progress < 20) {
        return (
            <Badge variant="outline" className="gap-1 px-1.5 text-muted-foreground">
                <TriangleAlertIcon className="size-3 text-red-500"/>
                Low
            </Badge>
        )
    }

    if (progress < 100) {
        return (
            <Badge variant="outline" className="gap-1 px-1.5 text-muted-foreground">
                <LoaderIcon className="size-3 text-orange-500"/>
                In Progress
            </Badge>
        )
    }

    if (progress === 100) {
        return (
            <Badge variant="outline" className="gap-1 px-1.5 text-muted-foreground">
                <CheckIcon className="size-3 text-green-500"/>
                Hit
            </Badge>
        )
    }

    if (progress > 100) {
        return (
            <Badge variant="outline" className="gap-1 px-1.5 text-muted-foreground">
                <CheckCheck className="size-3 text-green-500"/>
                Exceeded
            </Badge>
        )
    }
}