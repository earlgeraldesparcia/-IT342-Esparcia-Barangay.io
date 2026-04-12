# Backend Database Alignment with Supabase

## Issue Identified

Your backend was using H2 in-memory database while frontend was configured for Supabase PostgreSQL. The schemas were completely different.

## Changes Made

### 1. Database Configuration Updated
- Switched from H2 to Supabase PostgreSQL
- Updated Hibernate dialect to PostgreSQLDialect
- Disabled H2 console
- Configured proper connection parameters

### 2. Entity Classes Created
Created new entity classes matching Supabase schema:

#### UserProfile.java
- Maps to `user_profiles` table
- Links to Supabase auth users via `user_id`
- Supports role-based access (resident/administrator)

#### Resident.java  
- Maps to `residents` table
- References `user_profiles` via `user_profile_id`
- Contains resident-specific information

#### CertificateType.java
- Maps to `certificate_types` table
- Pre-populated with certificate types

#### Appointment.java
- Maps to `appointments` table  
- Supports appointment status management
- Links residents to certificate types

### 3. Repository Interfaces Created
- UserProfileRepository - for user profile management
- ResidentRepository - for resident data management
- (Need to create AppointmentRepository and CertificateTypeRepository)

## Next Steps Required

### 1. Complete Repository Creation
```java
// Create these repository interfaces:
- AppointmentRepository
- CertificateTypeRepository
```

### 2. Update Service Classes
- Modify AuthService to use new UserProfile and Resident entities
- Create AppointmentService for appointment management
- Update authentication flow to work with Supabase auth

### 3. Update Controllers
- Modify AuthController to handle new entity structure
- Create AppointmentController for appointment management
- Ensure role-based access control

### 4. Database Schema Synchronization
- Run the Supabase schema.sql in your Supabase project
- Ensure backend entities match exactly with Supabase tables
- Test entity creation and relationships

### 5. Authentication Integration
- Decide between:
  A) Use Supabase Auth exclusively (recommended)
  B) Use backend auth with Supabase database
- Update frontend accordingly

## Recommended Architecture

### Option A: Supabase Auth + Backend API (Recommended)
- Frontend uses Supabase Auth for authentication
- Backend validates JWT tokens from Supabase
- Backend handles business logic and data management
- Clean separation of concerns

### Option B: Backend-Only Auth
- Frontend authenticates through backend
- Backend manages users and passwords
- More complex but more control

## Testing Steps

1. **Start Backend**: Ensure PostgreSQL connection works
2. **Test Entity Creation**: Verify entities save to Supabase
3. **Test Authentication**: Verify user registration/login
4. **Test Frontend Integration**: Ensure frontend can call backend
5. **Test Full Flow**: End-to-end appointment booking

## Important Notes

- The old `User.java` entity should be deprecated
- New entities use UUID primary keys to match Supabase
- All timestamps use `created_at`/`updated_at` naming
- Role-based access is enforced at entity level

## Configuration Files Updated

- `application.properties`: Now uses Supabase PostgreSQL
- Database URL, credentials, and dialect configured
- H2 configuration commented out for reference

This alignment ensures your backend and frontend use the same database schema and can work together seamlessly.
