import { Outlet } from "react-router-dom";
import Sidebar from "./Sidebar";
import Footer from "./Footer";

function Layout() {
  return (
    <div className="app-layout">
      <Sidebar />

      <div className="app-content">
        <Outlet />
         <Footer />
      </div>
    </div>
  );
}

export default Layout;