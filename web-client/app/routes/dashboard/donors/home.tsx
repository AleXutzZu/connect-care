import {DonorDataTable} from "~/components/donors/donor-data-table";

export const handle = {
    pageName: "Donors Information",
};

export default function DonorsPage() {
    return (
        <div className="flex flex-1 flex-col">
            <title>Teledon | Donors</title>
            <div className="@container/main flex flex-1 flex-col gap-2">
                <div className="flex flex-col gap-4 py-4 md:gap-6 md:py-6">
                    <DonorDataTable/>
                </div>
            </div>
        </div>
    );
}
