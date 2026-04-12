import { Navigate, Outlet } from 'react-router-dom';
import { getStoredRole, homePathForRole } from '../utils/authDisplay';

export default function RequireResident() {
  if (!localStorage.getItem('token')) {
    return <Navigate to="/login" replace />;
  }
  if (getStoredRole() !== 'resident') {
    return <Navigate to={homePathForRole('admin')} replace />;
  }
  return <Outlet />;
}
