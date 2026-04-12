import { createContext, useContext, useState, useEffect } from 'react'
import { authService } from '../services/authService'

const AuthContext = createContext()

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [userProfile, setUserProfile] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    // Initialize auth state
    const initializeAuth = async () => {
      try {
        const profile = await authService.getCurrentUserUserProfile()
        setUserProfile(profile)
        setUser(profile?.authUser || null)
      } catch (error) {
        console.error('Error initializing auth:', error)
      } finally {
        setLoading(false)
      }
    }

    initializeAuth()

    // Listen for auth changes
    const { data: { subscription } } = authService.onAuthStateChange(async (event, session) => {
      setUser(session?.user ?? null)
      
      if (session?.user) {
        const profile = await authService.getCurrentUserUserProfile()
        setUserProfile(profile)
      } else {
        setUserProfile(null)
      }
    })

    return () => subscription.unsubscribe()
  }, [])

  const signIn = async (email, password) => {
    const result = await authService.signIn(email, password)
    if (result.success) {
      setUser(result.user)
      setUserProfile(result.profile)
    }
    return result
  }

  const signUp = async (email, password, fullName, role = 'resident', phone = '', address = '') => {
    return await authService.signUp(email, password, fullName, role, phone, address)
  }

  const signOut = async () => {
    const result = await authService.signOut()
    if (result.success) {
      setUser(null)
      setUserProfile(null)
    }
    return result
  }

  const updateProfile = async (updates) => {
    const result = await authService.updateProfile(updates)
    if (result.success) {
      setUserProfile(prev => ({ ...prev, ...result.profile }))
    }
    return result
  }

  const isAdministrator = () => {
    return userProfile?.role === 'administrator'
  }

  const isResident = () => {
    return userProfile?.role === 'resident'
  }

  const value = {
    user,
    userProfile,
    loading,
    signIn,
    signUp,
    signOut,
    updateProfile,
    isAdministrator,
    isResident,
  }

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}
