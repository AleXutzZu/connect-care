import {index, prefix, route, type RouteConfig} from "@react-router/dev/routes";

export default [
    index("routes/home.tsx"),
    route("/login", "routes/login.tsx"),

    route("dashboard", "routes/dashboard/layout.tsx", [
            index("routes/dashboard/home.tsx"),
            route("donors", "routes/dashboard/donors/home.tsx"),
            route("donations", "routes/dashboard/donations/home.tsx")
            // route("charities", "routes/charities.tsx"),
        ]
    ),

    ...prefix("api", [
        route("/charities", "routes/api/api.charities.ts"),
        route("/charities/:charityId", "routes/api/api.charity.ts"),
        route("/donors", "routes/api/api.donors.ts"),
        route("/donors/:donorId", "routes/api/api.donor.ts")
    ]),
] satisfies RouteConfig;
