import { Navigate, Outlet } from "react-router-dom";
import { getRole } from "../utils/session";

function RoleGuard({ allowedRoles }) {
  const role = getRole();

  if (!allowedRoles.includes(role)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return <Outlet />;
}

export default RoleGuard;