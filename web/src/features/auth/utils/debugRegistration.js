import { supabase } from './supabase'

export const debugRegistration = {
  // Test basic Supabase connection
  async testConnection() {
    console.log('🔍 Testing Supabase connection...')
    
    try {
      const { data, error } = await supabase.from('user_profiles').select('count').single()
      
      if (error) {
        console.error('❌ Connection test failed:', error)
        return { success: false, error: error.message }
      }
      
      console.log('✅ Supabase connection successful')
      return { success: true }
    } catch (err) {
      console.error('❌ Connection test error:', err)
      return { success: false, error: err.message }
    }
  },

  // Check if tables exist
  async checkTables() {
    console.log('🔍 Checking if tables exist...')
    
    const tables = ['user_profiles', 'residents', 'appointments', 'certificate_types']
    const results = {}
    
    for (const table of tables) {
      try {
        const { data, error } = await supabase.from(table).select('count').single()
        results[table] = {
          exists: !error,
          error: error?.message || null
        }
        console.log(`${error ? '❌' : '✅'} Table ${table}: ${error ? 'Missing/Error' : 'Exists'}`)
      } catch (err) {
        results[table] = { exists: false, error: err.message }
        console.log(`❌ Table ${table}: Missing/Error`)
      }
    }
    
    return results
  },

  // Check RLS policies
  async checkRLSPolicies() {
    console.log('🔍 Checking RLS policies...')
    
    try {
      const { data, error } = await supabase.rpc('get_table_policies', { table_name: 'user_profiles' })
      
      if (error) {
        console.error('❌ Cannot check RLS policies:', error)
        return { success: false, error: error.message }
      }
      
      console.log('✅ RLS policies found:', data?.length || 0)
      return { success: true, policies: data }
    } catch (err) {
      console.error('❌ RLS policy check error:', err)
      return { success: false, error: err.message }
    }
  },

  // Test user creation without RLS
  async testDirectUserInsert() {
    console.log('🔍 Testing direct user insert...')
    
    try {
      const testUser = {
        user_id: '00000000-0000-0000-0000-000000000000',
        role: 'resident',
        full_name: 'Test User',
        phone: '0917 123 4567',
        address: 'Test Address',
        is_active: true
      }
      
      const { data, error } = await supabase
        .from('user_profiles')
        .insert([testUser])
        .select()
        .single()
      
      if (error) {
        console.error('❌ Direct insert failed:', error)
        return { success: false, error: error.message }
      }
      
      console.log('✅ Direct insert successful:', data)
      
      // Clean up test data
      await supabase.from('user_profiles').delete().eq('user_id', testUser.user_id)
      
      return { success: true, data }
    } catch (err) {
      console.error('❌ Direct insert error:', err)
      return { success: false, error: err.message }
    }
  },

  // Test auth user creation
  async testAuthUserCreation() {
    console.log('🔍 Testing auth user creation...')
    
    try {
      const testEmail = `test-${Date.now()}@example.com`
      const testPassword = 'testPassword123'
      
      const { data, error } = await supabase.auth.signUp({
        email: testEmail,
        password: testPassword,
        options: {
          data: {
            full_name: 'Test Auth User',
            role: 'resident'
          }
        }
      })
      
      if (error) {
        console.error('❌ Auth user creation failed:', error)
        return { success: false, error: error.message }
      }
      
      console.log('✅ Auth user created:', data.user?.id)
      
      // Try to get the user
      const { data: userData, error: userError } = await supabase.auth.getUser()
      
      if (userError) {
        console.error('❌ Cannot get created user:', userError)
      } else {
        console.log('✅ Can retrieve created user:', userData.user?.id)
      }
      
      return { success: true, user: data.user, testEmail }
    } catch (err) {
      console.error('❌ Auth user creation error:', err)
      return { success: false, error: err.message }
    }
  },

  // Test profile creation with real auth user
  async testProfileCreation() {
    console.log('🔍 Testing profile creation with auth user...')
    
    try {
      // First create auth user
      const testEmail = `profile-test-${Date.now()}@example.com`
      const testPassword = 'testPassword123'
      
      const { data: authData, error: authError } = await supabase.auth.signUp({
        email: testEmail,
        password: testPassword,
        options: {
          data: {
            full_name: 'Profile Test User',
            role: 'resident',
            phone: '0917 987 6543',
            address: 'Profile Test Address'
          }
        }
      })
      
      if (authError) {
        console.error('❌ Auth user creation failed:', authError)
        return { success: false, error: authError.message }
      }
      
      console.log('✅ Auth user created for profile test')
      
      // Try to create profile (this should work with updated auth service)
      const { data: profileData, error: profileError } = await supabase
        .from('user_profiles')
        .insert([{
          user_id: authData.user.id,
          role: 'resident',
          full_name: 'Profile Test User',
          phone: '0917 987 6543',
          address: 'Profile Test Address',
        }])
        .select()
        .single()
      
      if (profileError) {
        console.error('❌ Profile creation failed:', profileError)
        
        // Try to get more details about the error
        console.log('🔍 Error details:', {
          code: profileError.code,
          details: profileError.details,
          hint: profileError.hint
        })
        
        return { success: false, error: profileError.message }
      }
      
      console.log('✅ Profile created successfully:', profileData)
      
      // Clean up
      await supabase.from('user_profiles').delete().eq('user_id', authData.user.id)
      
      return { success: true, profile: profileData }
    } catch (err) {
      console.error('❌ Profile creation error:', err)
      return { success: false, error: err.message }
    }
  },

  // Run all diagnostic tests
  async runFullDiagnostic() {
    console.log('🚀 Starting full registration diagnostic...')
    console.log('=' .repeat(50))
    
    const results = {}
    
    results.connection = await this.testConnection()
    console.log('')
    
    results.tables = await this.checkTables()
    console.log('')
    
    if (Object.values(results.tables).some(t => !t.exists)) {
      console.log('❌ Some tables are missing. Please run the schema first.')
      return results
    }
    
    results.rls = await this.checkRLSPolicies()
    console.log('')
    
    results.directInsert = await this.testDirectUserInsert()
    console.log('')
    
    results.authUser = await this.testAuthUserCreation()
    console.log('')
    
    results.profileCreation = await this.testProfileCreation()
    console.log('')
    
    console.log('=' .repeat(50))
    console.log('🏁 Diagnostic complete!')
    
    return results
  },

  // Check existing users and profiles
  async checkExistingData() {
    console.log('🔍 Checking existing data...')
    
    try {
      // Check auth users
      const { data: authUsers, error: authError } = await supabase.auth.admin.listUsers()
      
      if (authError) {
        console.error('❌ Cannot list auth users:', authError)
      } else {
        console.log(`✅ Found ${authUsers.users.length} auth users`)
        authUsers.users.forEach(user => {
          console.log(`   - ${user.email} (confirmed: ${!!user.email_confirmed_at})`)
        })
      }
      
      console.log('')
      
      // Check user profiles
      const { data: profiles, error: profileError } = await supabase
        .from('user_profiles')
        .select('*')
      
      if (profileError) {
        console.error('❌ Cannot fetch user profiles:', profileError)
      } else {
        console.log(`✅ Found ${profiles.length} user profiles`)
        profiles.forEach(profile => {
          console.log(`   - ${profile.full_name} (${profile.role})`)
        })
      }
      
      console.log('')
      
      // Check residents
      const { data: residents, error: residentError } = await supabase
        .from('residents')
        .select('*')
      
      if (residentError) {
        console.error('❌ Cannot fetch residents:', residentError)
      } else {
        console.log(`✅ Found ${residents.length} residents`)
        residents.forEach(resident => {
          console.log(`   - ${resident.name} (${resident.email})`)
        })
      }
      
      return {
        authUsers: authUsers?.users || [],
        profiles: profiles || [],
        residents: residents || []
      }
    } catch (err) {
      console.error('❌ Error checking existing data:', err)
      return { error: err.message }
    }
  }
}
