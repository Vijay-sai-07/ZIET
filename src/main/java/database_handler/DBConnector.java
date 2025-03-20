package database_handler;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnector {
    public static final Connection publicConnection = (new DBConnector("/home/padma-zstch1463/Desktop/new/Zeat100/src/main/java/database_handler/config")).connection;
    protected Connection connection;
        PrintStream printStream = System.out;
//    PrintStream printStream = null;

    public DBConnector(Connection connection) {
        this.connection = connection;
    }

    public DBConnector(String configFilePath) {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(configFilePath));
            String classPath = properties.getProperty("class_path");
            try {
                Class.forName(classPath);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            String url = properties.getProperty("url").replace("\\:", ":");
            String username = (String) properties.get("username");
            connection = DriverManager.getConnection(url + properties.getOrDefault("database_name", ""), username, (String) properties.get("password"));
        } catch (SQLException | IOException e) {
            System.err.println("There was an error in your username or password, check again");
            e.printStackTrace();
        }
    }

    public DBConnector(String classPath, String url, String username, String password) {
        try {
            try {
                Class.forName(classPath);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.err.println("There was an error in your username or password, check again");
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.err.println("Error closing the connection!!");
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
