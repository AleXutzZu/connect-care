import {index, prefix, route, type RouteConfig} from "@react-router/dev/routes";

export default [
    index("routes/home.tsx"),
    route("/login", "routes/login.tsx"),

    route("dashboard", "routes/dashboard/layout.tsx", [
            index("routes/dashboard/home.tsx"),
            route("donors", "routes/dashboard/donors/home.tsx"),
        ]
    ),

    ...prefix("api", [
        route("/charities", "routes/api/api.charities.ts"),
        route("/charities/:charityId", "routes/api/api.charity.ts"),
        route("/donors", "routes/api/api.donors.ts"),
        route("/donors/:donorId", "routes/api/api.donor.ts"),
        route("/statistics/donors/:donorId", "routes/api/api.statistics.donor.ts"),
        route("/donations", "routes/api/api.donations.ts"),
        route("/donations/:donationId", "routes/api/api.donation.ts"),
    ]),
] satisfies RouteConfig;
