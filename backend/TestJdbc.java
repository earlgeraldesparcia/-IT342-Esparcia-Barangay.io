import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class TestJdbc {
    public static void main(String[] args) {
        String[] urls = {
            "jdbc:postgresql://aws-0-ap-northeast-1.pooler.supabase.com:6543/postgres",
            "jdbc:postgresql://aws-0-ap-northeast-1.pooler.supabase.com:5432/postgres",
            "jdbc:postgresql://aws-1-ap-northeast-1.pooler.supabase.com:6543/postgres",
            "jdbc:postgresql://aws-1-ap-northeast-1.pooler.supabase.com:5432/postgres"
        };
        
        for (String url : urls) {
            System.out.println("Testing URL: " + url);
            Properties props = new Properties();
            props.setProperty("user", "postgres.wqisqoevshaohpclduxn");
            props.setProperty("password", "WrongPassword123");
            props.setProperty("ssl", "true");
            props.setProperty("sslmode", "require");
            props.setProperty("loginTimeout", "5"); // fail fast
            props.setProperty("socketTimeout", "5"); 
            
            try {
                Connection conn = DriverManager.getConnection(url, props);
                System.out.println("SUCCESS for " + url);
                conn.close();
            } catch (Exception e) {
                System.out.println("FAILED: " + e.getMessage());
            }
        }
    }
}
