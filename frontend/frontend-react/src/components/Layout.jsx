import { Outlet } from "react-router-dom";
import Sidebar from "./Sidebar";

function Layout() {
  return (
    <div className="app-layout">
      <Sidebar />

      <div className="app-content">
        <Outlet />
      </div>
    </div>
  );
}

export default Layout;