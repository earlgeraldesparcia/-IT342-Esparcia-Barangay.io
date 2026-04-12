-- Barangay Appointment System Database Schema
-- Run this in your Supabase SQL Editor

-- User profiles table - links Supabase auth users with roles
CREATE TABLE user_profiles (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
  role TEXT NOT NULL CHECK (role IN ('resident', 'administrator')),
  full_name TEXT,
  phone TEXT,
  address TEXT,
  is_active BOOLEAN DEFAULT true,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  UNIQUE(user_id)
);

-- Residents table - updated to link with user profiles
CREATE TABLE residents (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  user_profile_id UUID REFERENCES user_profiles(id) ON DELETE CASCADE,
  name TEXT NOT NULL,
  email TEXT UNIQUE,
  phone TEXT,
  address TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Certificate types table
CREATE TABLE certificate_types (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  name TEXT NOT NULL UNIQUE,
  description TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Appointments table
CREATE TABLE appointments (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  resident_id UUID REFERENCES residents(id) ON DELETE CASCADE,
  certificate_type_id UUID REFERENCES certificate_types(id),
  appointment_date DATE NOT NULL,
  appointment_time TIME NOT NULL,
  purpose TEXT NOT NULL,
  status TEXT DEFAULT 'confirmed' CHECK (status IN ('pending', 'confirmed', 'claimed', 'completed', 'no_show')),
  notes TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Insert sample certificate types
INSERT INTO certificate_types (name, description) VALUES
('Barangay Clearance', 'Proof of residency and good moral character'),
('Certificate of Indigency', 'Proof of low income for financial assistance'),
('Community Tax Certificate', 'Local tax payment certificate'),
('Business Permit', 'Permit to operate business in barangay'),
('Residence Certificate', 'Proof of current residence');

-- Create indexes for better performance
CREATE INDEX idx_appointments_date ON appointments(appointment_date);
CREATE INDEX idx_appointments_status ON appointments(status);
CREATE INDEX idx_appointments_resident ON appointments(resident_id);

-- Enable Row Level Security
ALTER TABLE user_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE residents ENABLE ROW LEVEL SECURITY;
ALTER TABLE appointments ENABLE ROW LEVEL SECURITY;
ALTER TABLE certificate_types ENABLE ROW LEVEL SECURITY;

-- User Profiles RLS Policies
-- Users can view their own profile
CREATE POLICY "Users can view own profile" ON user_profiles
  FOR SELECT USING (auth.uid() = user_id);

-- Users can update their own profile (limited fields)
CREATE POLICY "Users can update own profile" ON user_profiles
  FOR UPDATE USING (auth.uid() = user_id);

-- Only administrators can insert user profiles
CREATE POLICY "Administrators can create user profiles" ON user_profiles
  FOR INSERT WITH CHECK (
    EXISTS (
      SELECT 1 FROM user_profiles 
      WHERE user_id = auth.uid() AND role = 'administrator' AND is_active = true
    )
  );

-- Certificate Types RLS Policies
-- Allow public read access to certificate types
CREATE POLICY "Certificate types are viewable by everyone" ON certificate_types
  FOR SELECT USING (true);

-- Only administrators can manage certificate types
CREATE POLICY "Administrators can manage certificate types" ON certificate_types
  FOR ALL USING (
    EXISTS (
      SELECT 1 FROM user_profiles 
      WHERE user_id = auth.uid() AND role = 'administrator' AND is_active = true
    )
  );

-- Appointments RLS Policies
-- Administrators can view all appointments
CREATE POLICY "Administrators can view all appointments" ON appointments
  FOR SELECT USING (
    EXISTS (
      SELECT 1 FROM user_profiles 
      WHERE user_id = auth.uid() AND role = 'administrator' AND is_active = true
    )
  );

-- Residents can view their own appointments
CREATE POLICY "Residents can view own appointments" ON appointments
  FOR SELECT USING (
    EXISTS (
      SELECT 1 FROM residents r
      JOIN user_profiles up ON r.user_profile_id = up.id
      WHERE r.id = resident_id AND up.user_id = auth.uid() AND up.is_active = true
    )
  );

-- Administrators can insert appointments
CREATE POLICY "Administrators can create appointments" ON appointments
  FOR INSERT WITH CHECK (
    EXISTS (
      SELECT 1 FROM user_profiles 
      WHERE user_id = auth.uid() AND role = 'administrator' AND is_active = true
    )
  );

-- Residents can create their own appointments
CREATE POLICY "Residents can create own appointments" ON appointments
  FOR INSERT WITH CHECK (
    EXISTS (
      SELECT 1 FROM residents r
      JOIN user_profiles up ON r.user_profile_id = up.id
      WHERE r.id = resident_id AND up.user_id = auth.uid() AND up.is_active = true
    )
  );

-- Only administrators can update appointment status
CREATE POLICY "Administrators can update appointments" ON appointments
  FOR UPDATE USING (
    EXISTS (
      SELECT 1 FROM user_profiles 
      WHERE user_id = auth.uid() AND role = 'administrator' AND is_active = true
    )
  );

-- Residents RLS Policies
-- Administrators can view all residents
CREATE POLICY "Administrators can view all residents" ON residents
  FOR SELECT USING (
    EXISTS (
      SELECT 1 FROM user_profiles 
      WHERE user_id = auth.uid() AND role = 'administrator' AND is_active = true
    )
  );

-- Residents can view their own profile
CREATE POLICY "Residents can view own profile" ON residents
  FOR SELECT USING (
    EXISTS (
      SELECT 1 FROM user_profiles up
      WHERE up.user_id = auth.uid() AND up.id = user_profile_id AND up.is_active = true
    )
  );

-- Administrators can manage residents
CREATE POLICY "Administrators can manage residents" ON residents
  FOR ALL USING (
    EXISTS (
      SELECT 1 FROM user_profiles 
      WHERE user_id = auth.uid() AND role = 'administrator' AND is_active = true
    )
  );

-- Residents can update their own profile
CREATE POLICY "Residents can update own profile" ON residents
  FOR UPDATE USING (
    EXISTS (
      SELECT 1 FROM user_profiles up
      WHERE up.user_id = auth.uid() AND up.id = user_profile_id AND up.is_active = true
    )
  );
