# Empty Tables Fix Guide

## Issue: Tables Remain Empty After Registration

Your Supabase tables are empty despite successful registration attempts. Here's how to diagnose and fix this.

## 🔍 Step 1: Run Diagnostic Tool

I've created a comprehensive diagnostic tool to identify the exact issue:

### Add Debug Component Temporarily

1. **Add to your main App component:**
```jsx
import DebugRegistration from './src/components/DebugRegistration'

// Add this temporarily in your App routing
<Route path="/debug" element={<DebugRegistration />} />
```

2. **Navigate to `/debug`** in your browser
3. **Click "Run Full Diagnostic"**
4. **Check browser console** for detailed logs

### What the Diagnostic Tests:

1. **Supabase Connection** - Verifies your credentials work
2. **Table Existence** - Checks if schema was applied
3. **RLS Policies** - Tests if Row Level Security blocks inserts
4. **Direct Insert Test** - Tries inserting without auth
5. **Auth User Creation** - Tests Supabase auth system
6. **Profile Creation** - Tests full registration flow

## 🚨 Common Causes & Fixes

### Cause 1: Schema Not Applied
**Symptoms:** Tables don't exist
**Fix:**
```sql
-- Run this in Supabase SQL Editor
-- Copy entire contents of database/schema.sql and run it
```

### Cause 2: RLS Policies Blocking Inserts
**Symptoms:** Tables exist but no data can be inserted
**Fix:**
```sql
-- Temporarily disable RLS for testing
ALTER TABLE user_profiles DISABLE ROW LEVEL SECURITY;
ALTER TABLE residents DISABLE ROW LEVEL SECURITY;

-- Test registration, then re-enable:
ALTER TABLE user_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE residents ENABLE ROW LEVEL SECURITY;
```

### Cause 3: Auth User Not Created
**Symptoms:** Registration succeeds but no auth user
**Fix:**
1. Check **Supabase Dashboard** → **Authentication** → **Users**
2. Look for your test email
3. If not there, check email confirmation requirements

### Cause 4: Environment Variables Wrong
**Symptoms:** Connection errors
**Fix:**
```env
# Verify these are correct in your .env file
VITE_SUPABASE_URL=https://your-project.supabase.co
VITE_SUPABASE_PUBLISHABLE_KEY=your-real-key-here
```

### Cause 5: Email Confirmation Required
**Symptoms:** Auth user exists but no profile
**Fix:**
1. **Check your email** for confirmation link
2. **Click the link** to confirm
3. **Try signing in** - profile should be created

## 🔧 Quick Fix Steps

### Step 1: Verify Schema
```sql
-- Check if tables exist
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public' AND table_name IN ('user_profiles', 'residents', 'appointments');

-- Should return: user_profiles, residents, appointments
```

### Step 2: Check Auth Users
```sql
-- Check if auth users exist
SELECT id, email, created_at, email_confirmed_at 
FROM auth.users 
ORDER BY created_at DESC;
```

### Step 3: Test Manual Insert
```sql
-- Try inserting a test profile directly
INSERT INTO user_profiles (
  user_id,
  role,
  full_name,
  is_active
) VALUES (
  gen_random_uuid(),
  'resident',
  'Test User',
  true
);

-- If this works, the issue is in your app code
```

## 🎯 Most Likely Issues

Based on your symptoms, here are the most likely causes in order:

### 1. Schema Not Applied (80% probability)
- You haven't run the updated schema in Supabase SQL Editor
- **Fix:** Run the entire `database/schema.sql` file

### 2. Email Confirmation (15% probability)
- Users created but waiting for email confirmation
- **Fix:** Check email or disable confirmation temporarily

### 3. RLS Policies (5% probability)
- Policies are too restrictive and blocking inserts
- **Fix:** Temporarily disable RLS for testing

## 🚀 Immediate Actions

### Option 1: Quick Test (5 minutes)
1. Go to **Supabase Dashboard** → **SQL Editor**
2. Run: `SELECT COUNT(*) FROM user_profiles;`
3. If error: **Schema not applied** - run schema.sql
4. If returns 0: **Proceed to next option**

### Option 2: Disable Email Confirmation (2 minutes)
1. **Supabase Dashboard** → **Authentication** → **Settings**
2. **Disable "Enable email confirmations"**
3. **Save** and test registration again

### Option 3: Manual Profile Creation (3 minutes)
1. Create auth user manually in dashboard
2. Get user ID from auth.users table
3. Create profile manually with that ID

## 📊 Diagnostic Results Interpretation

When you run the diagnostic tool, look for:

- ✅ All green: Issue is likely email confirmation
- ❌ Table missing: Run schema.sql
- ❌ RLS blocking: Check policy permissions
- ❌ Auth failing: Check credentials

## 🔄 Next Steps After Fix

1. **Run diagnostic again** to verify fix
2. **Test registration** with real user data
3. **Verify profile creation** in database
4. **Test login** functionality
5. **Remove debug component** from your app

## 💡 Pro Tips

- **Always check browser console** for JavaScript errors
- **Use Supabase logs** in dashboard for detailed error info
- **Test with simple data** first (no special characters)
- **Clear browser cache** between tests
- **Use different emails** for each test

Run the diagnostic tool first, then follow the specific fix based on the results!
