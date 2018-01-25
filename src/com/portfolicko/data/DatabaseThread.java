package com.portfolicko.data;

/**
 * Created by enayethussain on 25/12/2017.
 */
public class DatabaseThread extends Thread {
    private String query;

    public DatabaseThread(String query) {
        this.query = query;
    }
    public void run() {
        final String url = "jdbc:mysql://127.0.0.1/portfolicko?autoReconnect=true&useSSL=false";
        final String username = "root";
        final String password = "7ef20a3179ada058fc535c5087e35b6b5f6d82f1fca25f45";
        new Database(url, username, password, query);
    }
}
