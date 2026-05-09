import {type RouteConfig, index, route, layout} from "@react-router/dev/routes";

export default [index("routes/home.tsx"),
    route("/login", "routes/login.tsx"),
    layout("routes/dashboard/layout.tsx", [
        route("/dashboard", "routes/dashboard/home.tsx",
            [])
    ])
] satisfies RouteConfig;
