import { MatxLoadable } from "matx";
import { authRoles } from "../../auth/authRoles";

const Profile = MatxLoadable({
  loader: () => import("../../MatxLayout/Layout1/Profile"),
});

const Analytics = MatxLoadable({
  loader: () => import("./Analytics")
})

const dashboardRoutes = [
  {
    path: "/dashboard/patients",
    component: Analytics,
    auth: authRoles.admin,
  },
  {
    path: "/doctor/profile",
    component: Profile,
  },
];

export default dashboardRoutes;
