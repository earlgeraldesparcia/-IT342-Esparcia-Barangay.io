# User Types Setup Guide

## Overview

Your barangay appointment system now supports two distinct user types:

1. **Administrators** - Can manage all appointments, residents, and system settings
2. **Residents** - Can only view and manage their own appointments

## Database Changes

### New Tables

- **`user_profiles`** - Links Supabase auth users with roles and profile information
- **`residents`** - Updated to reference user profiles
- **`appointments`** - Updated with role-based access controls

### Role-Based Permissions

#### Administrator Permissions
- View all appointments across all residents
- Update appointment status (confirmed, claimed, completed, no_show)
- View all resident profiles
- Create and manage resident accounts
- View system statistics (total appointments, residents, etc.)

#### Resident Permissions
- View only their own appointments
- Create new appointments for themselves
- Update their own profile information
- View their own appointment statistics

## Authentication Flow

### 1. User Registration
```javascript
// Register a new resident
const result = await authService.signUp(
  'email@example.com',
  'password123',
  'Juan Dela Cruz',
  'resident',
  '0917 123 4567',
  '123 Main St'
);

// Register a new administrator (only existing admins can do this)
const result = await authService.signUp(
  'admin@barangay.gov',
  'adminPassword',
  'Admin User',
  'administrator',
  '0917 987 6543',
  'Barangay Hall'
);
```

### 2. User Login
```javascript
const result = await authService.signIn('email@example.com', 'password123');
if (result.success) {
  console.log('User role:', result.profile.role);
}
```

### 3. Role-Based Access Control
```javascript
// In components
const { isAdministrator, isResident } = useAuth();

if (isAdministrator()) {
  // Show admin features
}

if (isResident()) {
  // Show resident features
}
```

## Database Schema Updates

Run the updated schema in your Supabase SQL Editor:

```sql
-- The schema has been updated to include:
-- 1. user_profiles table with role-based access
-- 2. Enhanced RLS policies for security
-- 3. Proper relationships between tables
```

## Component Protection

### Protected Routes
Use the route protection components:

```jsx
// Admin-only pages
<AdminRoute>
  <AdminAppointments />
</AdminRoute>

// Resident-only pages  
<ResidentRoute>
  <MyAppointments />
</ResidentRoute>

// Any authenticated user
<AuthenticatedRoute>
  <Profile />
</AuthenticatedRoute>
```

### In-Component Protection
```jsx
function AdminAppointments() {
  const { isAdministrator } = useAuth();
  
  if (!isAdministrator()) {
    return <div>Access Denied</div>;
  }
  
  // Admin content here
}
```

## Service Layer Updates

### Authentication Service (`authService.js`)
- `getCurrentUserProfile()` - Get user profile with role
- `isAdministrator()` / `isResident()` - Role checking helpers
- `signUp()` - Register users with roles
- `signIn()` / `signOut()` - Authentication methods
- `updateProfile()` - Update user information

### Appointment Service (`appointmentService.js`)
- All methods now include role-based validation
- Administrators can access all appointments
- Residents can only access their own appointments
- Enhanced error handling and security

## Setting Up Initial Administrator

### Method 1: Direct Database Insert
```sql
-- Create an admin user directly in Supabase
INSERT INTO user_profiles (
  user_id, -- Use the UUID from auth.users
  role,
  full_name,
  is_active
) VALUES (
  'your-user-uuid-here',
  'administrator',
  'System Administrator',
  true
);
```

### Method 2: Through the App (Recommended)
1. First create a regular user account
2. Manually update their role in the database to 'administrator'
3. Use that account to create other administrator accounts

## Migration Steps

1. **Update Database Schema**
   - Run the new schema in Supabase SQL Editor
   - This will create the user_profiles table and update RLS policies

2. **Update Environment Variables**
   - Ensure your `.env` file has the correct Supabase credentials

3. **Migrate Existing Data**
   - If you have existing residents, create user_profiles for them
   - Link existing resident records to new user profiles

4. **Update Application Code**
   - Wrap your app with `AuthProvider`
   - Add route protection to sensitive pages
   - Update components to use the new auth context

## Security Features

### Row Level Security (RLS)
- All tables have RLS enabled
- Policies enforce role-based access at the database level
- Users can only access data they're authorized to see

### Authentication Integration
- Uses Supabase Auth for secure authentication
- JWT tokens automatically handled
- Session management built-in

### Role Validation
- Role checks happen at both application and database level
- Prevents privilege escalation attempts
- Secure API endpoints

## Testing the Setup

### 1. Create Test Users
```javascript
// Test administrator
await authService.signUp('admin@test.com', 'password', 'Admin User', 'administrator');

// Test resident  
await authService.signUp('resident@test.com', 'password', 'Resident User', 'resident');
```

### 2. Test Access Control
- Log in as administrator - should see all appointments
- Log in as resident - should only see their own appointments
- Try accessing admin pages as resident - should be denied

### 3. Test Database Security
- Use Supabase SQL Editor to test RLS policies
- Verify users can only access their own data

## Next Steps

1. **Set up authentication providers** (Google, Email, etc.)
2. **Create user management interface** for administrators
3. **Add email notifications** for appointment updates
4. **Implement audit logging** for admin actions
5. **Create user dashboard** for residents

## Troubleshooting

### Common Issues

1. **"Access Denied" Errors**
   - Check user role in user_profiles table
   - Verify RLS policies are correctly applied
   - Ensure user is authenticated

2. **Permission Errors**
   - Verify user has is_active = true
   - Check role matches expected values ('resident' or 'administrator')
   - Review RLS policy conditions

3. **Missing User Profile**
   - User exists in auth.users but not in user_profiles
   - Create profile record manually or through registration
   - Check foreign key constraints

### Debugging Tips

- Use browser console to see auth state changes
- Check network requests for API errors
- Verify Supabase RLS policy execution in SQL Editor
- Test with different user roles to isolate issues
