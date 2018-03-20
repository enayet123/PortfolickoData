package com.portfolicko.data;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import java.sql.*;

/**
 * Created by Enayet Hussain on 24/12/2017.
 */
public class Database {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    final String DB_URL;

    //  Database credentials
    final String USER;
    final String PASS;

    // Connection and Statement
    Connection conn = null;
    Statement stmt = null;

    public Database(String url, String username, String password) {
        this.DB_URL = url;
        this.USER = username;
        this.PASS = password;

        try {
            // Register Driver
            Class.forName(JDBC_DRIVER);

            // Connect
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Create Statement
            stmt = conn.createStatement();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public Database(String url, String username, String password, String query) {
        this.DB_URL = url;
        this.USER = username;
        this.PASS = password;

        try {
            // Register Driver
            Class.forName("com.mysql.jdbc.Driver");

            // Connect
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Create Statement
            stmt = conn.createStatement();
            this.runInsert(query);
            this.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean runInsert(String sql) {
        // Run insert statement
        //System.out.println(sql);
        try {
            stmt.execute(sql);
        } catch (MySQLIntegrityConstraintViolationException e) {
            System.out.println("New coins to be added...");
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean close() {
        // Close all connections/objects
        try {
            conn.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
