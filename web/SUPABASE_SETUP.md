# Supabase Setup Guide for Barangay Appointment System

## 1. Create a Supabase Project

1. Go to [https://supabase.com](https://supabase.com)
2. Click "Start your project" 
3. Sign up/login with your GitHub account
4. Click "New Project"
5. Choose your organization
6. Enter project details:
   - **Project Name**: barangay-appointment-system
   - **Database Password**: Create a strong password
   - **Region**: Choose the closest region to your users
7. Click "Create new project"
8. Wait for the project to be ready (2-3 minutes)

## 2. Get Your Project Credentials

1. In your Supabase dashboard, go to **Settings** → **API**
2. Copy the following values:
   - **Project URL** (looks like `https://xxxxxxxx.supabase.co`)
   - **anon public** API key (starts with `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`)

## 3. Set Up Environment Variables

1. Copy the `.env.example` file to `.env`:
   ```bash
   cp .env.example .env
   ```

2. Edit the `.env` file and replace the placeholder values:
   ```env
   # Google OAuth Client ID
   VITE_GOOGLE_CLIENT_ID=589326447505-6qqub2g3v2bgtnk68itpel8sqd2qups4.apps.googleusercontent.com

   # Supabase Configuration
   VITE_SUPABASE_URL=https://your-project-id.supabase.co
   VITE_SUPABASE_ANON_KEY=your-anon-key-here
   ```

## 4. Set Up the Database Schema

1. In your Supabase dashboard, go to **SQL Editor**
2. Click "New query"
3. Copy the entire contents of `database/schema.sql`
4. Paste it into the SQL Editor
5. Click "Run" to execute the schema

This will create:
- `residents` table - stores resident information
- `certificate_types` table - stores available certificate types
- `appointments` table - stores appointment records
- Row Level Security (RLS) policies
- Sample certificate types

## 5. Test the Connection

1. Start your development server:
   ```bash
   npm run dev
   2. Navigate to the admin appointments page
   3. You should see "Loading appointments..." initially, then empty states once connected

## 6. Add Sample Data (Optional)

To test with sample data, run this in the SQL Editor:

```sql
-- Insert sample residents
INSERT INTO residents (name, email, phone, address) VALUES
('Juan Dela Cruz', 'juan@example.com', '0917 000 0000', '123 Main St, Barangay Poblacion'),
('Maria Santos', 'maria@example.com', '0918 111 2222', '456 Oak Ave, Barangay Poblacion'),
('Pedro Reyes', 'pedro@example.com', '0922 333 4444', '789 Pine Rd, Barangay Poblacion');

-- Insert sample appointments
INSERT INTO appointments (resident_id, certificate_type_id, appointment_date, appointment_time, purpose, status) VALUES
(1, 1, CURRENT_DATE, '09:30:00', 'Employment requirement', 'confirmed'),
(2, 2, CURRENT_DATE, '10:15:00', 'Medical assistance', 'claimed'),
(3, 3, CURRENT_DATE, '14:00:00', 'Renewal', 'confirmed');
```

## 7. Authentication Setup (Optional)

If you want to add user authentication later:

1. Go to **Authentication** → **Settings**
2. Enable the authentication providers you need (Google, Email, etc.)
3. Update your RLS policies to work with authenticated users

## 8. Production Considerations

When deploying to production:

1. **Enable Database Backups**: Go to Settings → Database → Backups
2. **Review RLS Policies**: Ensure they match your security requirements
3. **Set Up Custom Domain**: Available in Pro plans
4. **Monitor Usage**: Keep an eye on your database usage and API calls

## Troubleshooting

### Common Issues:

1. **"Missing Supabase environment variables" error**
   - Make sure your `.env` file is in the root directory
   - Check that the variable names match exactly
   - Restart your development server after changing `.env`

2. **"Invalid API key" error**
   - Double-check that you copied the correct anon key
   - Ensure there are no extra spaces or characters

3. **"Permission denied" errors**
   - Check your RLS policies in the SQL Editor
   - Make sure tables have RLS enabled properly

4. **Connection issues**
   - Verify your Supabase project URL is correct
   - Check that your project is active (not paused)

### Getting Help:

- Check the browser console for detailed error messages
- Review the Supabase documentation at [https://supabase.com/docs](https://supabase.com/docs)
- The SQL Editor in Supabase dashboard has query history and error details

## Next Steps

Once Supabase is set up, you can:
1. Add more complex appointment management features
2. Implement user authentication
3. Add email notifications for appointments
4. Create reporting and analytics features
5. Add file upload capabilities for document requirements
