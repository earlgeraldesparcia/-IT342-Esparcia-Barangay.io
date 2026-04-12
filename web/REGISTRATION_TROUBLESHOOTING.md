# Registration Troubleshooting Guide

## Issue: Registration Successful but No Account in Supabase

This is a common issue that occurs when email confirmation is required but not handled properly.

## Root Cause

Supabase requires email confirmation by default, but the original code tried to create user profiles immediately after auth user creation, before email confirmation.

## Solution Implemented

The auth service has been updated to properly handle email confirmation:

### 1. **Email Detection**
- Registration now checks if email confirmation is needed
- Returns `needsConfirmation: true` when email not confirmed
- Stores user data in metadata for later profile creation

### 2. **Automatic Profile Creation**
- When user confirms email and signs in, profile is automatically created
- Uses stored metadata to populate user profile and resident records
- Handles the transition from unconfirmed to confirmed state

### 3. **Enhanced Error Handling**
- Better error messages and user feedback
- Proper state management during confirmation process

## How to Fix Current Issue

### Method 1: Disable Email Confirmation (Quick Fix)

1. Go to your **Supabase Dashboard**
2. Navigate to **Authentication** → **Settings**
3. Find **Enable email confirmations** and **disable it**
4. Click **Save**

### Method 2: Handle Email Confirmation (Recommended)

1. **Check your email** for the confirmation link
2. **Click the confirmation link** in the email
3. **Try signing in** to your account
4. The profile should be created automatically

### Method 3: Manual Profile Creation

If you need to create profiles for existing unconfirmed users:

```sql
-- Run this in Supabase SQL Editor
-- Find users without profiles
SELECT 
  au.id as user_id,
  au.email,
  au.created_at,
  au.email_confirmed_at,
  au.user_metadata
FROM auth.users au
LEFT JOIN user_profiles up ON au.id = up.user_id
WHERE up.user_id IS NULL;

-- Create profile for specific user (replace UUID)
INSERT INTO user_profiles (
  user_id,
  role,
  full_name,
  phone,
  address,
  is_active
) VALUES (
  'user-uuid-here',
  'resident',
  'User Name',
  'phone',
  'address',
  true
);
```

## Testing the Fix

### 1. Test Registration Flow
```javascript
// Test registration
const result = await authService.signUp(
  'test@example.com',
  'password123',
  'Test User',
  'resident',
  '0917 123 4567',
  'Test Address'
);

console.log(result);
// Should show: { success: true, needsConfirmation: true, message: '...' }
```

### 2. Test Email Confirmation
1. Check email for confirmation link
2. Click link to confirm email
3. Sign in with credentials
4. Profile should be created automatically

### 3. Verify in Database
```sql
-- Check if user profile was created
SELECT * FROM user_profiles WHERE user_id = 'your-user-id';

-- Check if resident record was created
SELECT * FROM residents WHERE user_profile_id = 'profile-id';
```

## Common Issues and Solutions

### Issue 1: No Confirmation Email
**Solution:**
- Check spam/junk folder
- Verify email address is correct
- Check Supabase email settings in Authentication → Email Templates

### Issue 2: Confirmation Link Expired
**Solution:**
- Request new confirmation email
- Or disable email confirmation temporarily for testing

### Issue 3: Profile Still Not Created After Confirmation
**Solution:**
- Check browser console for errors
- Verify user metadata is complete
- Manually trigger profile creation using `handleEmailConfirmation()`

### Issue 4: RLS Policy Blocking Profile Creation
**Solution:**
- Ensure user is properly authenticated
- Check RLS policies allow profile creation
- Temporarily disable RLS for testing

## Database Configuration Check

### Verify Email Settings
1. **Supabase Dashboard** → **Authentication** → **Settings**
2. Check **Site URL** is correct
3. Verify **Email templates** are configured
4. Test **Email delivery** if available

### Check RLS Policies
```sql
-- Verify policies are correctly applied
SELECT 
  schemaname,
  tablename,
  policyname,
  permissive,
  roles,
  cmd,
  qual
FROM pg_policies 
WHERE schemaname = 'public' AND tablename IN ('user_profiles', 'residents');
```

## Development vs Production

### Development Environment
- You can disable email confirmation for easier testing
- Use test emails that you can access
- Monitor browser console for debugging

### Production Environment
- Keep email confirmation enabled for security
- Set up proper email templates
- Monitor failed registrations

## Next Steps

1. **Test the updated registration flow**
2. **Verify email confirmation works**
3. **Check automatic profile creation**
4. **Test both resident and administrator registration**
5. **Monitor error logs for any issues**

## Support

If you continue to have issues:

1. **Check browser console** for JavaScript errors
2. **Check Supabase logs** in the dashboard
3. **Verify database schema** is up to date
4. **Test with different email addresses**

The updated authentication system should now properly handle the email confirmation flow and create user profiles automatically when users confirm their email addresses.
