import { Navigate, Outlet } from 'react-router-dom';
import { getStoredRole, homePathForRole } from '../utils/authDisplay';

export default function RequireAdmin() {
  if (!localStorage.getItem('token')) {
    return <Navigate to="/login" replace />;
  }
  if (getStoredRole() !== 'admin') {
    return <Navigate to={homePathForRole('resident')} replace />;
  }
  return <Outlet />;
}
