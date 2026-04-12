import { supabase } from '../utils/supabase'

export const authService = {
  // Get current user profile with role
  async getCurrentUserProfile() {
    const { data: { user } } = await supabase.auth.getUser()
    if (!user) return null

    const { data, error } = await supabase
      .from('user_profiles')
      .select('*')
      .eq('user_id', user.id)
      .eq('is_active', true)
      .single()

    if (error) {
      console.error('Error fetching user profile:', error)
      return null
    }

    return { ...data, authUser: user }
  },

  // Check if current user is administrator
  async isAdministrator() {
    const profile = await this.getCurrentUserProfile()
    return profile?.role === 'administrator'
  },

  // Check if current user is resident
  async isResident() {
    const profile = await this.getCurrentUserProfile()
    return profile?.role === 'resident'
  },

  // Get user role
  async getUserRole() {
    const profile = await this.getCurrentUserProfile()
    return profile?.role || null
  },

  // Sign up with email and create user profile
  async signUp(email, password, fullName, role = 'resident', phone = '', address = '') {
    try {
      // Create auth user with metadata
      const { data: authData, error: authError } = await supabase.auth.signUp({
        email,
        password,
        options: {
          data: {
            full_name: fullName,
            role: role,
            phone: phone,
            address: address
          }
        }
      })

      if (authError) throw authError
      if (!authData.user) throw new Error('Failed to create user')

      // Check if user needs email confirmation
      if (authData.user && !authData.user.email_confirmed_at) {
        return { 
          success: true, 
          user: authData.user, 
          profile: null,
          needsConfirmation: true,
          message: 'Please check your email to confirm your account'
        }
      }

      // If email is confirmed or not required, create user profile
      const { data: profileData, error: profileError } = await supabase
        .from('user_profiles')
        .insert([{
          user_id: authData.user.id,
          role,
          full_name: fullName,
          phone,
          address,
        }])
        .select()
        .single()

      if (profileError) throw profileError

      // If role is resident, also create resident record
      if (role === 'resident') {
        const { error: residentError } = await supabase
          .from('residents')
          .insert([{
            user_profile_id: profileData.id,
            name: fullName,
            email,
            phone,
            address,
          }])

        if (residentError) throw residentError
      }

      return { success: true, user: authData.user, profile: profileData, needsConfirmation: false }
    } catch (error) {
      console.error('Error signing up:', error)
      return { success: false, error: error.message }
    }
  },

  // Handle email confirmation and create user profile
  async handleEmailConfirmation() {
    try {
      const { data: { user } } = await supabase.auth.getUser()
      if (!user) throw new Error('No authenticated user')

      // Check if profile already exists
      const { data: existingProfile } = await supabase
        .from('user_profiles')
        .select('*')
        .eq('user_id', user.id)
        .single()

      if (existingProfile) {
        return { success: true, profile: existingProfile }
      }

      // Create user profile using metadata stored during signup
      const metadata = user.user_metadata
      const { data: profileData, error: profileError } = await supabase
        .from('user_profiles')
        .insert([{
          user_id: user.id,
          role: metadata.role || 'resident',
          full_name: metadata.full_name || user.email?.split('@')[0],
          phone: metadata.phone || '',
          address: metadata.address || '',
        }])
        .select()
        .single()

      if (profileError) throw profileError

      // If role is resident, also create resident record
      if (profileData.role === 'resident') {
        const { error: residentError } = await supabase
          .from('residents')
          .insert([{
            user_profile_id: profileData.id,
            name: profileData.full_name,
            email: user.email,
            phone: profileData.phone,
            address: profileData.address,
          }])

        if (residentError) throw residentError
      }

      return { success: true, profile: profileData }
    } catch (error) {
      console.error('Error handling email confirmation:', error)
      return { success: false, error: error.message }
    }
  },

  // Sign in
  async signIn(email, password) {
    try {
      const { data, error } = await supabase.auth.signInWithPassword({
        email,
        password,
      })

      if (error) throw error

      // Get user profile
      const profile = await this.getCurrentUserProfile()
      
      return { success: true, user: data.user, profile }
    } catch (error) {
      console.error('Error signing in:', error)
      return { success: false, error: error.message }
    }
  },

  // Sign out
  async signOut() {
    try {
      const { error } = await supabase.auth.signOut()
      if (error) throw error
      return { success: true }
    } catch (error) {
      console.error('Error signing out:', error)
      return { success: false, error: error.message }
    }
  },

  // Update user profile
  async updateProfile(updates) {
    try {
      const { data: { user } } = await supabase.auth.getUser()
      if (!user) throw new Error('No authenticated user')

      const { data, error } = await supabase
        .from('user_profiles')
        .update(updates)
        .eq('user_id', user.id)
        .select()
        .single()

      if (error) throw error

      // If updating resident info, also update residents table
      if (updates.full_name || updates.phone || updates.address) {
        const { error: residentError } = await supabase
          .from('residents')
          .update({
            name: updates.full_name,
            phone: updates.phone,
            address: updates.address,
          })
          .eq('user_profile_id', data.id)

        if (residentError) throw residentError
      }

      return { success: true, profile: data }
    } catch (error) {
      console.error('Error updating profile:', error)
      return { success: false, error: error.message }
    }
  },

  // Listen to auth state changes
  onAuthStateChange(callback) {
    return supabase.auth.onAuthStateChange(async (event, session) => {
      // Handle email confirmation event
      if (event === 'SIGNED_IN' && session?.user) {
        const profile = await this.getCurrentUserUserProfile()
        if (!profile && session.user.email_confirmed_at) {
          // User confirmed email but profile doesn't exist
          await this.handleEmailConfirmation()
        }
      }
      callback(event, session)
    })
  }
}
