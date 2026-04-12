import { Navigate } from 'react-router-dom';
import { getStoredRole, homePathForRole } from '../utils/authDisplay';

export default function LegacyDashboardRedirect() {
  if (!localStorage.getItem('token')) {
    return <Navigate to="/login" replace />;
  }
  return <Navigate to={homePathForRole(getStoredRole())} replace />;
}
