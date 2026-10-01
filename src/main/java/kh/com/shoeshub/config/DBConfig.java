package kh.com.shoeshub.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DBConfig {

    private static final DBConfig INSTANCE = new DBConfig();

    // Automatically reads application.properties from classpath
    private final ResourceBundle rb = ResourceBundle.getBundle("application");

    private DBConfig() {}

    public static DBConfig getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                rb.getString("db.url"),
                rb.getString("db.user"),
                rb.getString("db.password")
        );
    }

    public static Connection get() throws SQLException {
        return INSTANCE.getConnection();
    }
}
