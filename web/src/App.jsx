import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import Login from './features/auth/pages/Login'
import Registration from './features/auth/pages/Registration'
import ResidentDashboardLayout from './features/resident/pages/ResidentDashboardLayout'
import AdminDashboardLayout from './features/admin/pages/AdminDashboardLayout'
import DashboardHome from './features/resident/pages/DashboardHome'
import ResidentAppointments from './features/resident/pages/ResidentAppointments'
import AdminDashboardHome from './features/admin/pages/AdminDashboardHome'
import AdminAppointments from './features/admin/pages/AdminAppointments'
import BookingForm from './features/appointment/pages/BookingForm'
import Settings from './features/settings/pages/Settings'
import RequireAuth from './features/auth/components/RequireAuth'
import RequireResident from './features/auth/components/RequireResident'
import RequireAdmin from './features/auth/components/RequireAdmin'
import LegacyDashboardRedirect from './core/components/LegacyDashboardRedirect'
import './App.css'

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Registration />} />

        <Route path="/dashboard/*" element={<LegacyDashboardRedirect />} />

        <Route element={<RequireAuth />}>
          <Route path="/settings" element={<Settings />} />
        </Route>

        <Route element={<RequireResident />}>
          <Route path="/resident" element={<ResidentDashboardLayout />}>
            <Route index element={<Navigate to="dashboard" replace />} />
            <Route path="dashboard" element={<DashboardHome />} />
            <Route path="appointments" element={<ResidentAppointments />} />
            <Route path="book" element={<BookingForm />} />
          </Route>
        </Route>

        <Route element={<RequireAdmin />}>
          <Route path="/admin" element={<AdminDashboardLayout />}>
            <Route index element={<Navigate to="dashboard" replace />} />
            <Route path="dashboard" element={<AdminDashboardHome />} />
            <Route path="appointments" element={<AdminAppointments />} />
          </Route>
        </Route>

        <Route path="/" element={<Navigate to="/login" replace />} />
      </Routes>
    </Router>
  )
}

export default App
