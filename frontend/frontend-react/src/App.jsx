import {BrowserRouter, Navigate, Route, Routes,} from "react-router-dom";
import ProtectedRoute from "./components/ProtectedRoute";
import RoleGuard from "./components/RoleGuard";
import Layout from "./components/Layout";
import Login from "./pages/Login";
import Home from "./pages/Home";
import Dashboard from "./pages/Dashboard";
import Unauthorized from "./pages/Unauthorized";
import Incidents from "./pages/Incidents";
import IncidentCreate from "./pages/IncidentCreate";
import IncidentDetails from "./pages/IncidentDetails";
import IncidentEdit from "./pages/IncidentEdit";
import Audits from "./pages/Audits";
import UserProfile from "./pages/UserProfile";
import AuditCreate from "./pages/AuditCreate";
import AuditDetails from "./pages/AuditDetails";
import AuditEdit from "./pages/AuditEdit";
import CorrectiveActions from "./pages/CorrectiveActions";
import CorrectiveActionDetails from "./pages/CorrectiveActionDetails";
import CorrectiveActionCreate from "./pages/CorrectiveActionCreate";
import CorrectiveActionEdit from "./pages/CorrectiveActionEdit";
import Departments from "./pages/Departments";
import Users from "./pages/Users";
import UserEdit from "./pages/UserEdit";
import UserRole from "./pages/UserRole";
import UserDelete from "./pages/UserDelete";
import Register from "./pages/Register";
import Reports from "./pages/Reports";
import MonthlyAdmissions from "./pages/MonthlyAdmissions";


function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route element={<ProtectedRoute />}>
          <Route element={<Layout />}>
            <Route path="/" element={<Home />} />
            <Route path="/incidents" element={<Incidents />} />
            <Route path="/incidents/new" element={<IncidentCreate />} />
            <Route path="/incidents/:id" element={<IncidentDetails />} />
            <Route path="/departments" element={<Departments />} />
            <Route path="/unauthorized" element={<Unauthorized />} />
            <Route path="/User/profile"  element={<UserProfile/>} />

           <Route element={<RoleGuard allowedRoles={["ADMIN"]} />}>
                          <Route path="/users" element={<Users />} />
                          <Route path="/users/:id/edit" element={<UserEdit />} />
                          <Route path="/users/:id/role" element={<UserRole />} />
                          <Route path="/users/:id/delete" element={<UserDelete />} />
           </Route>

           <Route element={ <RoleGuard allowedRoles={["ADMIN", "QUALITY_MANAGER"]} /> } >
                          <Route path="/audits" element={<Audits />} />
                          <Route path="/audits/new" element={<AuditCreate />} />
                          <Route path="/audits/:id" element={<AuditDetails />} />
                          <Route path="/audits/:id/edit" element={<AuditEdit />} />
                          <Route path="/monthly-admissions" element={<MonthlyAdmissions />} />
           </Route>

            <Route element={  <RoleGuard allowedRoles={["ADMIN", "QHSE_MANAGER", "STAFF"]} /> } >
                            <Route path="/corrective-actions"  element={<CorrectiveActions />} />
                            <Route path="/corrective-actions/:id" element={<CorrectiveActionDetails />} />
            </Route>

            <Route element={  <RoleGuard allowedRoles={["ADMIN", "QHSE_MANAGER"]} />  } >
                             <Route  path="/corrective-actions/new" element={<CorrectiveActionCreate />}  />
                             <Route path="/corrective-actions/:id/edit" element={<CorrectiveActionEdit />} />
            </Route>

            <Route  element={ <RoleGuard allowedRoles={[ "ADMIN", "QUALITY_MANAGER",  "QHSE_MANAGER",]}/> } >
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/incidents/:id/edit" element={<IncidentEdit />} />
              <Route path="/reports" element={<Reports />} />
            </Route>
          </Route>
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>

      
    
    </BrowserRouter>
    
  );
}

export default App;