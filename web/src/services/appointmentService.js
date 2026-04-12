import { supabase } from '../utils/supabase'
import { authService } from './authService'

export const appointmentService = {
  // Get appointments for a specific date (role-based)
  async getAppointmentsByDate(date) {
    try {
      const userRole = await authService.getUserRole()
      
      let query = supabase
        .from('appointments')
        .select(`
          id,
          appointment_time,
          purpose,
          status,
          notes,
          appointment_date,
          residents!inner (
            id,
            name,
            email,
            phone
          ),
          certificate_types!inner (
            id,
            name
          )
        `)
        .eq('appointment_date', date)
        .order('appointment_time', { ascending: true })

      const { data, error } = await query

      if (error) {
        console.error('Error fetching appointments:', error)
        throw new Error('Failed to fetch appointments')
      }

      return data.map(appointment => ({
        id: appointment.id,
        name: appointment.residents.name,
        time: appointment.appointment_time,
        cert: appointment.certificate_types.name,
        purpose: appointment.purpose,
        phone: appointment.residents.phone,
        email: appointment.residents.email,
        status: appointment.status,
        residentId: appointment.residents.id,
        certificateTypeId: appointment.certificate_types.id,
        notes: appointment.notes,
        date: appointment.appointment_date
      }))
    } catch (error) {
      console.error('Error in getAppointmentsByDate:', error)
      throw error
    }
  },

  // Update appointment status (administrators only)
  async updateAppointmentStatus(appointmentId, status) {
    try {
      // Check if user is administrator
      const isAdmin = await authService.isAdministrator()
      if (!isAdmin) {
        throw new Error('Only administrators can update appointment status')
      }

      const { data, error } = await supabase
        .from('appointments')
        .update({ 
          status,
          updated_at: new Date().toISOString()
        })
        .eq('id', appointmentId)
        .select()
        .single()

      if (error) {
        console.error('Error updating appointment status:', error)
        throw new Error('Failed to update appointment status')
      }

      return data
    } catch (error) {
      console.error('Error in updateAppointmentStatus:', error)
      throw error
    }
  },

  // Get appointment statistics for a date (administrators only)
  async getAppointmentStats(date) {
    try {
      // Check if user is administrator
      const isAdmin = await authService.isAdministrator()
      if (!isAdmin) {
        // For residents, return only their own stats
        const profile = await authService.getCurrentUserUserProfile()
        if (!profile) {
          throw new Error('User profile not found')
        }

        const { data, error } = await supabase
          .from('appointments')
          .select('status')
          .eq('appointment_date', date)
          .eq('resident_id', profile.resident_id)

        if (error) {
          console.error('Error fetching resident appointment stats:', error)
          throw new Error('Failed to fetch appointment stats')
        }

        return {
          total: data.length,
          confirmed: data.filter(apt => apt.status === 'confirmed').length,
          claimed: data.filter(apt => apt.status === 'claimed').length,
          completed: data.filter(apt => apt.status === 'completed').length,
          no_show: data.filter(apt => apt.status === 'no_show').length,
          pending: data.filter(apt => apt.status === 'pending').length
        }
      }

      // For administrators, get all stats
      const { data, error } = await supabase
        .from('appointments')
        .select('status')
        .eq('appointment_date', date)

      if (error) {
        console.error('Error fetching appointment stats:', error)
        throw new Error('Failed to fetch appointment stats')
      }

      return {
        total: data.length,
        confirmed: data.filter(apt => apt.status === 'confirmed').length,
        claimed: data.filter(apt => apt.status === 'claimed').length,
        completed: data.filter(apt => apt.status === 'completed').length,
        no_show: data.filter(apt => apt.status === 'no_show').length,
        pending: data.filter(apt => apt.status === 'pending').length
      }
    } catch (error) {
      console.error('Error in getAppointmentStats:', error)
      throw error
    }
  },

  // Get total number of residents (administrators only)
  async getTotalResidents() {
    try {
      // Check if user is administrator
      const isAdmin = await authService.isAdministrator()
      if (!isAdmin) {
        return 0 // Residents don't need to see total count
      }

      const { count, error } = await supabase
        .from('residents')
        .select('*', { count: 'exact', head: true })

      if (error) {
        console.error('Error fetching residents count:', error)
        throw new Error('Failed to fetch residents count')
      }

      return count || 0
    } catch (error) {
      console.error('Error in getTotalResidents:', error)
      throw error
    }
  },

  // Create new appointment (role-based)
  async createAppointment(appointmentData) {
    try {
      const userRole = await authService.getUserRole()
      
      // If resident, ensure they can only create appointments for themselves
      if (userRole === 'resident') {
        const profile = await authService.getCurrentUserUserProfile()
        if (!profile) {
          throw new Error('User profile not found')
        }
        
        // Ensure resident_id matches their own profile
        appointmentData.resident_id = profile.resident_id
      }

      const { data, error } = await supabase
        .from('appointments')
        .insert([appointmentData])
        .select()
        .single()

      if (error) {
        console.error('Error creating appointment:', error)
        throw new Error('Failed to create appointment')
      }

      return data
    } catch (error) {
      console.error('Error in createAppointment:', error)
      throw error
    }
  },

  // Create or update resident (administrators only)
  async upsertResident(residentData) {
    try {
      // Check if user is administrator
      const isAdmin = await authService.isAdministrator()
      if (!isAdmin) {
        throw new Error('Only administrators can manage residents')
      }

      const { data, error } = await supabase
        .from('residents')
        .upsert(residentData)
        .select()
        .single()

      if (error) {
        console.error('Error upserting resident:', error)
        throw new Error('Failed to update resident')
      }

      return data
    } catch (error) {
      console.error('Error in upsertResident:', error)
      throw error
    }
  }
}
