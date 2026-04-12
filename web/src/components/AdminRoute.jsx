import { Navigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'

export function AdminRoute({ children }) {
  const { userProfile, loading } = useAuth()

  if (loading) {
    return <div>Loading...</div>
  }

  if (!userProfile) {
    return <Navigate to="/login" replace />
  }

  if (userProfile.role !== 'administrator') {
    return <Navigate to="/unauthorized" replace />
  }

  return children
}

export function ResidentRoute({ children }) {
  const { userProfile, loading } = useAuth()

  if (loading) {
    return <div>Loading...</div>
  }

  if (!userProfile) {
    return <Navigate to="/login" replace />
  }

  if (userProfile.role !== 'resident') {
    return <Navigate to="/unauthorized" replace />
  }

  return children
}

export function AuthenticatedRoute({ children }) {
  const { userProfile, loading } = useAuth()

  if (loading) {
    return <div>Loading...</div>
  }

  if (!userProfile) {
    return <Navigate to="/login" replace />
  }

  return children
}
