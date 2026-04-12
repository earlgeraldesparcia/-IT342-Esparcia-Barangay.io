# MySQL Setup for Barangay.io Backend

## Prerequisites
1. **MySQL Server** installed and running
2. **MySQL Workbench** installed
3. **MySQL port 3306** open

## Step 1: Create Database in MySQL Workbench

1. **Open MySQL Workbench**
2. **Connect to MySQL Server**:
   - Hostname: `localhost`
   - Port: `3306`
   - Username: `root`
   - Password: `your_mysql_root_password`

3. **Create new database**:
   ```sql
   CREATE DATABASE barangay_db;
   ```

4. **Verify database created**:
   ```sql
   SHOW DATABASES;
   ```

## Step 2: Update Application Properties

Your `application.properties` is already configured for MySQL:

```properties
# MySQL Database Configuration (using MySQL Workbench)
spring.datasource.url=jdbc:mysql://localhost:3306/barangay_db
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

**Replace `your_mysql_password` with your actual MySQL root password.**

## Step 3: Start Backend

1. **Open terminal** in backend directory
2. **Run**: `mvn spring-boot:run`
3. **Check logs** for successful startup

## Step 4: Test Database Connection

Once backend starts, it should:
- Create tables automatically (Hibernate DDL)
- Connect to MySQL database
- Show SQL statements in console

## Step 5: Verify Tables

Check MySQL Workbench to see tables created:
```sql
USE barangay_db;
SHOW TABLES;
```

Expected tables:
- `users` (if using old User entity)
- `user_profiles` 
- `residents`
- `appointments`
- `certificate_types`

## Troubleshooting

### Connection Failed:
1. **Check MySQL service** is running
2. **Verify port 3306** is available
3. **Check username/password** combination
4. **Update password** in application.properties

### Table Creation Issues:
1. **Check Hibernate dialect** is set to `MySQLDialect`
2. **Verify DDL auto** is set to `update`
3. **Check SQL logs** for errors

## Quick Test Commands

**Test database connection:**
```sql
SELECT 1;
```

**Check tables:**
```sql
SHOW TABLES FROM barangay_db;
```

**Check users table:**
```sql
SELECT * FROM users LIMIT 5;
```

## Next Steps After Setup

1. **Backend should start successfully**
2. **Test registration endpoint** at `http://localhost:8080/api/auth/register`
3. **Verify data appears** in MySQL database
4. **Test login functionality**

The backend is now configured to use MySQL instead of Supabase, which should resolve the connection issues.
