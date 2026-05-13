import {index, prefix, route, type RouteConfig} from "@react-router/dev/routes";

export default [
    index("routes/home.tsx"),
    route("/login", "routes/login.tsx"),

    route("dashboard", "routes/dashboard/layout.tsx", [
            index("routes/dashboard/home.tsx"),
            // route("charities", "routes/charities.tsx"),
        ]
    ),

    ...prefix("api", [
        route("charities", "routes/api/api.charities.ts"),
        route("/charities/:charityId", "routes/api/api.charity.ts"),
    ]),
] satisfies RouteConfig;
