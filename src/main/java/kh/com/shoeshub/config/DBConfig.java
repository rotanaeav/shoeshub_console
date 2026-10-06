package kh.com.shoeshub.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

//public class DBConfig {
//
//    private static final DBConfig INSTANCE = new DBConfig();
//
//    // Automatically reads application.properties from classpath
//    private final ResourceBundle rb = ResourceBundle.getBundle("application");
//
//    private DBConfig() {}
//
//    public static DBConfig getInstance() {
//        return INSTANCE;
//    }
//
//    public Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(
//                rb.getString("db.url"),
//                rb.getString("db.user"),
//                rb.getString("db.password")
//        );
//    }
//
//    public static Connection get() throws SQLException {
//        return INSTANCE.getConnection();
//    }
//}

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DBConfig {

    private static final DBConfig INSTANCE = new DBConfig();

    private final HikariDataSource dataSource;

    private DBConfig() {
        // Automatically reads application.properties from classpath
        ResourceBundle rb = ResourceBundle.getBundle("application");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(rb.getString("db.url"));
        config.setUsername(rb.getString("db.user"));
        config.setPassword(rb.getString("db.password"));

        config.setMaximumPoolSize(5);              // at most 5 open connections
        config.setMinimumIdle(1);                  // keep 1 ready
        config.setConnectionTimeout(10_000);       // fail after 10 s instead of 30 s
        config.setKeepaliveTime(120_000);          // ping idle connections every 2 min
        config.setMaxLifetime(300_000);            // replace connections every 5 min
        config.setInitializationFailTimeout(-1);   // don't crash at startup if the DB is unreachable

        dataSource = new HikariDataSource(config);
        Runtime.getRuntime().addShutdownHook(new Thread(dataSource::close));
    }

    public static DBConfig getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static Connection get() throws SQLException {
        return INSTANCE.getConnection();
    }
}