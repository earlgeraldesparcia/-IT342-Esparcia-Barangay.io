import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import Login from './pages/Login'
import Registration from './pages/Registration'
import ResidentDashboardLayout from './pages/ResidentDashboardLayout'
import AdminDashboardLayout from './pages/AdminDashboardLayout'
import DashboardHome from './pages/DashboardHome'
import ResidentAppointments from './pages/ResidentAppointments'
import AdminDashboardHome from './pages/AdminDashboardHome'
import AdminAppointments from './pages/AdminAppointments'
import BookingForm from './pages/BookingForm'
import Settings from './pages/Settings'
import RequireAuth from './components/RequireAuth'
import RequireResident from './components/RequireResident'
import RequireAdmin from './components/RequireAdmin'
import LegacyDashboardRedirect from './components/LegacyDashboardRedirect'
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
