# Supabase Connection Fix for Backend

## Issue Identified
The backend is failing to connect to Supabase with `SocketTimeoutException: Read timed out`. This is a common issue with Java applications connecting to cloud databases.

## Root Causes and Solutions

### 1. Connection String Format
The correct Supabase connection format should be:
```properties
spring.datasource.url=jdbc:postgresql://aws-1-ap-northeast-1.pooler.supabase.com:5432/postgres
spring.datasource.username=postgres.wqisqoevshaohpclduxn
spring.datasource.password=Barangay@1403202003
```

### 2. Network/SSL Issues
Supabase requires SSL connections which can cause timeout issues in Java.

## Solution Options

### Option 1: Add SSL Parameters (Recommended)
Add these parameters to your connection string:
```properties
spring.datasource.url=jdbc:postgresql://aws-1-ap-northeast-1.pooler.supabase.com:5432/postgres?ssl=true&sslmode=require
```

### Option 2: Use Direct Connection (Bypass Pooler)
Use the direct connection instead of pooler:
```properties
spring.datasource.url=jdbc:postgresql://aws-1-ap-northeast-1.supabase.com:5432/postgres
```

### Option 3: Add SSL Configuration
Add these properties:
```properties
spring.datasource.properties.ssl=true
spring.datasource.properties.sslmode=require
spring.datasource.properties.sslfactory=org.postgresql.ssl.DefaultJavaSSLFactory
```

### Option 4: Increase Timeouts
Increase connection timeouts:
```properties
spring.datasource.hikari.connection-timeout=120000
spring.datasource.hikari.socket-timeout=60000
```

## Testing Connection

### Step 1: Test with psql (if available)
```bash
psql "postgresql://postgres.wqisqoevshaohpclduxn:Barangay@1403202003@aws-1-ap-northeast-1.pooler.supabase.com:5432/postgres"
```

### Step 2: Test with Simple Java Program
Create a simple test to verify connection works outside Spring Boot.

### Step 3: Check Supabase Settings
1. Go to Supabase Dashboard
2. Settings > Database
3. Check "Connection parameters"
4. Verify the connection string format

## Alternative: Use Supabase Client Library

Instead of JDBC, consider using Supabase Java client:

```xml
<dependency>
    <groupId>io.supabase</groupId>
    <artifactId>supabase-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick Fix Steps

1. **Update connection string** with SSL parameters
2. **Add SSL configuration** properties
3. **Increase timeouts** for connection
4. **Test connection** with simple program
5. **Start backend** and verify connection

## If Still Fails

1. **Check network connectivity** - can you reach Supabase from your machine?
2. **Verify credentials** - are username/password correct?
3. **Check firewall/proxy** - might be blocking connection
4. **Try different port** - 6543 instead of 5432
5. **Contact Supabase support** - might be a regional issue

The connection timeout suggests the network connection is being established but SSL handshake is failing, which is why SSL parameters are important.
